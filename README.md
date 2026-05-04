#  Online Auction — CI/CD Demo
### Capítulo 15: Continuous Integration and Continuous Deployment

Pipeline CI/CD completo usando **GitHub Actions** con:
- **Backend:** Spring Boot 3 + Maven
- **Frontend:** Angular 17
- **Contenedor:** Docker → GitHub Container Registry (ghcr.io)

---

##  Estructura del Proyecto

```
cicd-demo/
├── .github/
│   └── workflows/
│       └── ci-cd.yml           Pipeline principal
├── backend/
│   ├── src/
│   │   ├── main/java/com/demo/auction/
│   │   │   ├── AuctionApplication.java
│   │   │   ├── controller/
│   │   │   │   ├── ProductController.java
│   │   │   │   └── HealthController.java
│   │   │   ├── model/Product.java
│   │   │   └── service/ProductService.java
│   │   └── resources/application.properties
│   ├── src/test/
│   │   └── ProductControllerTest.java  ← 8 tests unitarios
│   ├── Dockerfile                       ← Multi-stage build
│   └── pom.xml
├── frontend/
│   ├── src/
│   │   ├── app/app.component.ts
│   │   └── environments/
│   └── package.json
└── README.md
```

---

##  Cómo usar este proyecto

### Paso 1: Subir a GitHub

```bash
# Clonar o descomprimir este proyecto
cd cicd-demo

# Inicializar git y subir
git init
git add .
git commit -m "Initial commit: CI/CD demo setup"

# Crear repo en GitHub y conectar
git remote add origin https://github.com/TU_USUARIO/auction-cicd-demo.git
git branch -M main
git push -u origin main
```

### Paso 2: El pipeline se activa automáticamente

Al hacer `push` a `main`, GitHub Actions ejecutará:

```
┌─────────────────────────────────────────────────────────┐
│                  GitHub Actions Pipeline                  │
├──────────────┬──────────────┬──────────────────────────-─┤
│  Backend CI  │  Frontend CI │     Docker Build & Push     │
│              │              │                             │
│ mvn compile  │ npm install  │  docker build (amd64+arm64) │
│ mvn test     │ ng build     │  push → ghcr.io             │
│ mvn package  │ ng test      │                             │
└──────────────┴──────────────┴─────────────────────────────┘
```

### Paso 3: Ver los resultados

1. Ve a tu repositorio en GitHub
2. Click en la pestaña **Actions**
3. Verás el pipeline con todos los stages en verde 

---

##  Ejecutar localmente

### Backend (Spring Boot)

```bash
cd backend
mvn clean spring-boot:run
```

Verificar:
```bash
# Health check
curl http://localhost:8080/v1/api/health

# Listar productos
curl http://localhost:8080/v1/api/products

# Solo productos activos
curl http://localhost:8080/v1/api/products?activeOnly=true
```

### Tests unitarios

```bash
cd backend
mvn test
```

### Frontend (Angular)

```bash
cd frontend
npm install
npm start
# Abre http://localhost:4200
```

---

##  Docker

### Build local

```bash
cd backend
docker build -t auction-backend:local .
docker run -p 8080:8080 auction-backend:local
```

### Imagen publicada (después del pipeline)

```bash
docker pull ghcr.io/TU_USUARIO/auction-backend:latest
docker run -p 8080:8080 ghcr.io/TU_USUARIO/auction-backend:latest
```

---

##  Diferencia vs. el libro (Capítulo 15)

| Característica | Libro (Jenkins) | Este proyecto (GitHub Actions) |
|---|---|---|
| Servidor CI/CD | Jenkins local | GitHub Actions (cloud) |
| Tunnel público | ngrok | No necesario |
| Registry | Docker Hub | ghcr.io (incluido en GitHub) |
| Config pipeline | Jenkinsfile | `.github/workflows/ci-cd.yml` |
| Multi-plataforma | amd64 + arm64 |  amd64 + arm64 |
| Trigger automático | GitHub Webhook | Nativo en GitHub |
| Costo | Gratis (local) | Gratis (2000 min/mes) |

---

##  Secrets necesarios

| Secret | Valor | Dónde configurar |
|---|---|---|
| `GITHUB_TOKEN` | Automático  | No necesitas hacer nada |

Para Docker Hub (alternativa al libro):
| Secret | Valor |
|---|---|
| `DOCKERHUB_USERNAME` | Tu usuario de Docker Hub |
| `DOCKERHUB_TOKEN` | Access Token de Docker Hub |

---

##  Referencia al libro

Este proyecto implementa prácticamente los conceptos del **Capítulo 15**:

- **CI (Continuous Integration):** Jobs `backend-ci` y `frontend-ci`
  - Maven compila, testea y empaqueta automáticamente
  - Angular se construye en modo producción
  
- **CD (Continuous Delivery):** Job `docker-build-push`
  - Imagen Docker publicada en cada merge a `main`
  - Multi-arquitectura (amd64 + arm64)

- **Jenkinsfile → workflow YAML:** Misma estructura declarativa,
  diferente sintaxis

//Pruebas