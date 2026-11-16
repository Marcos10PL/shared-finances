# Wspólne finanse (shared-finances)

A web app for managing finances together: shared wallets, member roles, income and expenses, a monthly limit and summaries. University project (UŚ, PAW 1).

- `backend/`: Java 21, Spring Boot, Spring Data JPA, PostgreSQL
- `frontend/`: Nuxt 4, Nuxt UI, Tailwind, TypeScript (SPA)

## Prerequisites

- **JDK 21**: check with `java -version`. You don't need to install Maven, because the project includes the Maven Wrapper.
- **Node.js 24**: the version is in [frontend/.nvmrc](frontend/.nvmrc). With nvm, run `nvm use` inside `frontend/`.
- **pnpm**: run `corepack enable` once and the version pinned in `package.json` is used automatically.
- **Database credentials**: ask a team member for them (see below).

## Configuration and secrets

Settings are passed through `.env` files, which are **gitignored and must never be committed**. Each app has a committed `.env.example` template listing the variables it needs.

The team uses a shared online PostgreSQL database. **Get the connection details and the JWT secret from a team member over a private channel.**.

## Running the backend

1. Create the env file from the template:

   ```bash
   cd backend
   cp .env.example .env
   ```

2. Fill in `backend/.env`:

   | Variable      | Description                                                                 |
   |---------------|-----------------------------------------------------------------------------|
   | `DB_URL`      | JDBC URL, e.g. `jdbc:postgresql://<host>:5432/<database>`                   |
   | `DB_USERNAME` | Database user                                                               |
   | `DB_PASSWORD` | Database password                                                           |
   | `JWT_SECRET`  | Secret for signing tokens, **at least 32 characters** (shorter ones fail at startup) |

3. Start the app **from the `backend/` directory**, because `.env` is read from the current working directory:

   ```bash
   ./mvnw spring-boot:run        # Linux / macOS / Git Bash
   .\mvnw.cmd spring-boot:run    # Windows PowerShell
   ```

   If you use IntelliJ or VS Code instead, set the run configuration's working directory to `backend/`.

The API runs on **http://localhost:8080**. Hibernate creates or updates the tables automatically (`ddl-auto=update`). Sample requests are in [backend/http/](backend/http/) and can be run in IntelliJ or with the VS Code REST Client extension.

### Using your own local database (optional)

If you don't want to work on the shared database, run PostgreSQL locally, create an empty database (`CREATE DATABASE shared_finances;`) and point `DB_URL` at `jdbc:postgresql://localhost:5432/shared_finances`.

## Running the frontend

1. Install dependencies and create the env file:

   ```bash
   cd frontend
   pnpm install
   cp .env.example .env
   ```

2. `NUXT_PUBLIC_API_BASE` in `frontend/.env` is the backend address. The default is `http://localhost:8080`.

3. Start the dev server:

   ```bash
   pnpm dev
   ```

The app runs on **http://localhost:3000**. Keep that port: the backend's CORS configuration only allows `http://localhost:3000`.

Other scripts: `pnpm lint`, `pnpm format`, `pnpm typecheck`, `pnpm build`.

## Troubleshooting

- **`Could not resolve placeholder 'DB_URL'`**: the backend was started outside `backend/`, so `.env` wasn't found.
- **`WeakKeyException`**: `JWT_SECRET` is shorter than 32 characters.
- **`Connection refused` / authentication failed**: the `DB_*` values are wrong, or the database isn't reachable from your network.
- **CORS errors in the browser**: the frontend isn't running on `localhost:3000`, or the backend isn't running.
- **Port 8080 is taken**: add `server.port=8081` to `backend/.env` and update `NUXT_PUBLIC_API_BASE` to match. CORS doesn't need changing, because it only restricts the frontend's origin.
