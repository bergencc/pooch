# Pooch Scan

Pooch Scan is a student-built platform for scanning dog products and surfacing health and sustainability insights for pet owners. This repository includes the backend API, admin portal, and Android mobile app.

## Stewardship Update

This project was originally built by the Bergen Open Source Foundation (BOSF) for the Interdisciplinary Business Club (IBC) at Bergen Community College.

Moving forward, BOSF is the primary team responsible for:

- Deployment and hosting
- Maintenance and operations
- Roadmap improvements and feature development

## Repository Structure

- `backend/` - FastAPI service, PostgreSQL + Redis integrations, auth, scan and admin APIs
- `admin-portal/` - React + Vite web interface for management workflows
- `android/` - Kotlin + Jetpack Compose Android client
- `docker-compose.yml` - Local multi-service stack for backend, database, cache, and admin portal

## Local Development

### Quick Start (Docker)

```bash
docker compose up --build
```

Local endpoints:

- Admin portal: `http://localhost:3000`
- API: `http://localhost:8000`
- API docs: `http://localhost:8000/docs`

### Run Services Individually

Backend:

```bash
cd backend
uv sync
uv run uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
```

Admin portal:

```bash
cd admin-portal
bun install
bun run dev --host
```

Android app:

- Open `android/` in Android Studio
- Build and run the `app` module on an emulator or device

## How to Get Involved

1. Explore this repository and open issues.
2. Read code and existing workflows.
3. Start with small contributions (docs, testing, UI, refactors).
4. Participate in discussions and propose improvements.

If you are new to open source, documentation, testing, and design contributions are excellent entry points.

## Values

- Collaboration over competition
- Learning over perfection
- Openness over gatekeeping
- Creativity across disciplines
- Students building for students

## Contact

- Open a GitHub discussion in the BOSF organization
- Reach out through Bergen Community College communication channels

## License

Unless otherwise stated, project materials are released under open-source licenses. See [LICENSE.md](LICENSE.md) for details.
