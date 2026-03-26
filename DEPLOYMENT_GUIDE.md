# Deployment & Operations Guide

## Quick Start (Development)

### 1. Clone and Build
```bash
cd /home/saidur/projects/bip/BIP_SEP_GR2
git clone <repo>  # Already done
cd parking
mvn clean install
```

### 2. Start Services
```bash
# Terminal 1: Start Docker services
docker-compose up

# Terminal 2: Run backend (from parking directory)
# or use IDE to run ParkingApplication.java

# Terminal 3: Run frontend (from parking_front directory)
npm run dev
```

### 3. Access Application
- Frontend: http://localhost:5173
- Backend API: http://localhost:8080
- Database UI: http://localhost:8081 (Adminer)
- API Docs: http://localhost:8080/swagger-ui.html (if enabled)

---

## Testing

### Run All Tests
```bash
cd parking
mvn test
```

### Run Specific Test Class
```bash
mvn test -Dtest=ModularityVerificationTest         # Architecture tests
mvn test -Dtest=RestIntegrationTest                # REST API tests
```

### Run Tests with Coverage (Optional)
First add to pom.xml in `<build><plugins>`:
```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.8</version>
    <executions>
        <execution>
            <goals><goal>prepare-agent</goal></goals>
        </execution>
        <execution>
            <phase>test</phase>
            <goals><goal>report</goal></goals>
        </execution>
    </executions>
</plugin>
```

Then run:
```bash
mvn clean test
# Coverage report: target/site/jacoco/index.html
```

---

## Seed Data

### Load Seed Data on Startup

Set environment variable before starting:
```bash
export SQL_INIT_MODE=always
docker-compose up
```

### Seed Data Contents

**Users** (with plaintext passwords provided for reference):
- `citizen@example.com` / `TestPassword123` (role: CITIZEN)
- `admin@example.com` / `TestPassword123` (role: ADMIN)

**Zones**:
- Downtown Dortmund (51.5136, 7.4653) - 4 spaces
- Central Station (51.5170, 7.4640) - 2 spaces

**Spaces**:
- Mix of parking-only and EV-charging enabled
- Mix of SLOW_CHARGER, FAST_CHARGER, and FALSE statuses

### Manual Data Loading (If Needed)

```bash
# Connect to PostgreSQL
docker exec -it db-postgres psql -U admin -d parking_db

# Run SQL script
\i /db/seed-data.sql
```

---

## Environment Variables (Production)

Create `.env` file or set in deployment:

```bash
# Database
POSTGRES_HOST=production-db.example.com
POSTGRES_PORT=5432
POSTGRES_DB=parking_prod
POSTGRES_USER=parking_user
POSTGRES_PASSWORD=SecurePassword123

# Redis
REDIS_HOST=production-redis.example.com
REDIS_PORT=6379
REDIS_PASSWORD=RedisPassword123

# Mail
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=parkingsystem@gmail.com
MAIL_PASSWORD=GeneratedAppPassword

# Security
JWT_SECRET=YourVerySecureJWTSecretKeyAtLeast32Characters123!@#
CORS_ALLOWED_ORIGINS=https://parking.example.com,https://app.example.com

# Payment
PAYMENT_MOCK_MODE=false
PAYMENT_SERVICE_URL=https://api.stripe.com

# External Services
MAPPING_SERVICE_URL=https://mapping-api.example.com
NOTIFICATION_SERVICE_URL=https://notification-api.example.com
```

---

## Docker Deployment

### Production Docker Compose

Create `docker-compose.prod.yml`:

```yaml
version: '3.8'
services:
  backend:
    image: parking-backend:latest
    ports:
      - "8080:8080"
    environment:
      POSTGRES_URL: jdbc:postgresql://db:5432/parking_prod
      POSTGRES_USER: parking_user
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
      REDIS_HOST: redis
      JWT_SECRET: ${JWT_SECRET}
      CORS_ALLOWED_ORIGINS: ${CORS_ALLOWED_ORIGINS}
      MAIL_HOST: ${MAIL_HOST}
      MAIL_USERNAME: ${MAIL_USERNAME}
      MAIL_PASSWORD: ${MAIL_PASSWORD}
    depends_on:
      - db
      - redis
    restart: always
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/api/zones"]
      interval: 30s
      timeout: 10s
      retries: 3

  frontend:
    image: parking-frontend:latest
    ports:
      - "80:3000"
    environment:
      VITE_API_URL: https://api.parking.example.com
    depends_on:
      - backend
    restart: always

  db:
    image: postgres:15
    environment:
      POSTGRES_DB: parking_prod
      POSTGRES_USER: parking_user
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
    volumes:
      - postgres_prod:/var/lib/postgresql/data
    ports:
      - "5432:5432"
    restart: always

  redis:
    image: redis:latest
    command: redis-server --requirepass ${REDIS_PASSWORD}
    volumes:
      - redis_prod:/data
    ports:
      - "6379:6379"
    restart: always

volumes:
  postgres_prod:
  redis_prod:
```

Start production:
```bash
docker-compose -f docker-compose.prod.yml up --build
```

---

## Kubernetes Deployment (Optional)

### 1. Create ConfigMap for Configuration

```bash
kubectl create configmap parking-config \
  --from-literal=MAIL_HOST=smtp.gmail.com \
  --from-literal=CORS_ALLOWED_ORIGINS=https://parking.example.com \
  --from-literal=PAYMENT_MOCK_MODE=false
```

### 2. Create Secret for Sensitive Data

```bash
kubectl create secret generic parking-secrets \
  --from-literal=POSTGRES_PASSWORD=secure-password \
  --from-literal=JWT_SECRET=super-secure-jwt-key-32-chars+ \
  --from-literal=REDIS_PASSWORD=redis-password \
  --from-literal=MAIL_PASSWORD=mail-app-password
```

### 3. Create Deployment YAML

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: parking-backend
  labels:
    app: parking
spec:
  replicas: 3
  selector:
    matchLabels:
      app: parking
  template:
    metadata:
      labels:
        app: parking
    spec:
      containers:
      - name: backend
        image: parking-backend:latest
        ports:
        - containerPort: 8080
        env:
        - name: POSTGRES_HOST
          value: postgres-service
        - name: POSTGRES_PORT
          value: "5432"
        - name: POSTGRES_DB
          value: parking_prod
        - name: POSTGRES_USER
          valueFrom:
            configMapKeyRef:
              name: parking-config
              key: POSTGRES_USER
        - name: POSTGRES_PASSWORD
          valueFrom:
            secretKeyRef:
              name: parking-secrets
              key: POSTGRES_PASSWORD
        - name: REDIS_HOST
          value: redis-service
        - name: REDIS_PASSWORD
          valueFrom:
            secretKeyRef:
              name: parking-secrets
              key: REDIS_PASSWORD
        - name: JWT_SECRET
          valueFrom:
            secretKeyRef:
              name: parking-secrets
              key: JWT_SECRET
        livenessProbe:
          httpGet:
            path: /api/zones
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /api/zones
            port: 8080
          initialDelaySeconds: 5
          periodSeconds: 5
```

Deploy:
```bash
kubectl apply -f parking-deployment.yaml
```

---

## Database Backups

### PostgreSQL Backup

```bash
# Backup
docker exec db-postgres pg_dump -U admin parking_db > backup_$(date +%Y%m%d_%H%M%S).sql

# Schedule daily backup (add to crontab)
0 2 * * * docker exec db-postgres pg_dump -U admin parking_db > /backups/parking_$(date +\%Y\%m\%d).sql
```

### Restore from Backup

```bash
docker exec -i db-postgres psql -U admin parking_db < backup_20240326.sql
```

### Redis Backup

```bash
# Built-in RDB backup
docker exec parking-redis redis-cli --rdb /data/dump.rdb

# AOF (Append-Only File) is enabled by default
# Check volumes for persistence
```

---

## Monitoring

### Health Checks

```bash
# Backend health
curl http://localhost:8080/api/zones

# Database health
docker exec db-postgres pg_isready -U admin

# Redis health
docker exec parking-redis redis-cli ping
# Should return: PONG
```

### Logs

```bash
# Backend logs
docker logs springboot-backend

# Database logs
docker logs db-postgres

# Redis logs
docker logs parking-redis

# Combined logs
docker-compose logs -f
```

### Metrics (Optional - Add to pom.xml)

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

Then access: http://localhost:8080/actuator/prometheus

---

## Troubleshooting

### Backend Won't Start

```bash
# Check logs
docker logs springboot-backend

# Common issues:
# - Redis/DB not running: docker-compose up
# - Port already in use: lsof -i :8080
# - Invalid JWT_SECRET: Must be 32+ characters
```

### Database Connection Failed

```bash
# Check PostgreSQL
docker exec db-postgres psql -U admin -d parking_db -c "SELECT 1"

# Check Redis connection
docker exec parking-redis redis-cli ping

# Verify network
docker network ls
docker network inspect app-network
```

### Session Not Persisting

```bash
# Check Redis
docker exec parking-redis redis-cli
> KEYS *
> GET <session-key>

# Check Redis memory
docker exec parking-redis redis-cli INFO memory
```

### Performance Issues

```bash
# Check database queries
docker exec db-postgres psql -U admin -c "SELECT * FROM pg_stat_statements ORDER BY total_time DESC LIMIT 10"

# Check connection pool
# In logs, look for "HikariCP - Connection is not available"

# Increase pool size in application.yaml:
# spring.datasource.hikari.maximum-pool-size: 20
```

---

## Security Checklist

- [ ] JWT_SECRET is 32+ characters
- [ ] POSTGRES_PASSWORD is strong (16+ chars, mixed)
- [ ] CORS_ALLOWED_ORIGINS restricted to known domains
- [ ] HTTPS enabled in production
- [ ] Database backups encrypted
- [ ] Redis password set (REDIS_PASSWORD)
- [ ] Mail credentials from environment variables
- [ ] PAYMENT_MOCK_MODE=false only in production
- [ ] Rate limiting configured
- [ ] SQL injection prevention (parameterized queries only)
- [ ] CSRF tokens enabled for form submissions
- [ ] Secrets not in version control

---

## Scaling

### Horizontal Scaling

Because sessions are stored in Redis:
- Add more backend instances
- Route through load balancer (HAProxy/Nginx)
- All instances connect to same Redis
- Sessions automatically shared

### Vertical Scaling

Increase resources in docker-compose:

```yaml
backend:
  deploy:
    resources:
      limits:
        cpus: '2'
        memory: 2G
```

### Database Scaling

For read-heavy workloads, add read replicas in PostgreSQL.

---

## Rollback Procedure

If deployment fails:

```bash
# Revert to previous image
docker-compose up -d --build  # Rebuild, don't use previous

# Or restore from backup
docker exec -i db-postgres psql -U admin parking_db < backup_20240325.sql

# Stop services
docker-compose down

# Restore and start
docker-compose up
```

## Support Contacts

- Technical Issues: tech-support@parking.example.com
- Database Admin: dba@parking.example.com
- Infrastructure: ops@parking.example.com
