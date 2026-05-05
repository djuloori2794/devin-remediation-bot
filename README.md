# Devin Remediation Bot

> An event-driven automation that uses [Devin](https://devin.ai) to remediate GitHub issues in a fork of Apache Superset.

---

## The Problem

Engineering teams accumulate small but important work that often gets deprioritized because it requires engineer time:

- Dependency hygiene
- Lint / code quality issues
- Minor bugs and docs fixes

This service automates that work end-to-end:

- **Detects** issues automatically via GitHub labels
- **Delegates** remediation to Devin
- **Tracks** progress and outcomes through a REST API

---

## How It Works

```text
   GitHub Issues  ──(labeled: devin-remediate)
         │
         ▼
   Scheduled Scanner  ──(every 2 minutes)
         │
         ▼
   Java Orchestrator  ──(this service)
         │
         ▼
     Devin API        ──(creates session)
         │
         ▼
   Devin works        ──▶ opens PR / comments on issue
         │
         ▼
   Metrics + Task Tracking  ──(exposed via REST API)
```

---

## Setup

### 1. Clone the repository

```bash
git clone <your-repo-url>
cd devin-remediation-bot
```

### 2. Create environment configuration

```bash
cp .env.example .env
```

Update `.env` with your values:

```env
DEVIN_API_KEY=your_devin_api_key
GITHUB_TOKEN=your_github_token
GITHUB_OWNER=your_github_username
GITHUB_REPO=superset
SCAN_LABEL=devin-remediate
SCAN_INTERVAL_MS=120000
```

### 3. Where to get these values

| Variable | How to get it |
|---|---|
| `DEVIN_API_KEY` | Devin → **Settings** → **API Keys** → generate a new (legacy) API key |
| `GITHUB_TOKEN` | GitHub → **Settings** → **Developer settings** → **Personal access tokens (classic)** → generate with `repo` scope |
| `GITHUB_OWNER` | Your GitHub username (e.g. `your-username` if your repo is `https://github.com/your-username/superset`) |
| `GITHUB_REPO`  | Name of your forked repository — for this project, use `superset` |
| `SCAN_LABEL`   | The GitHub label that triggers remediation. Default: `devin-remediate` |
| `SCAN_INTERVAL_MS` | Scan frequency in milliseconds. Default: `120000` (2 minutes) |

---

## Simulating the Workflow

### Step 1 — Create an issue

In your fork of Apache Superset (`https://github.com/<your-username>/superset`), create an issue. For example:

> **Title:** Improve clarity of backend error message

Then add the label:

```text
devin-remediate
```

### Step 2 — Trigger automation

Either wait for the scheduled scan (every 2 minutes), or trigger it manually:

```bash
curl -X POST http://localhost:8080/scan-now
```

### Step 3 — Observe results

**View tasks:**

```bash
curl http://localhost:8080/tasks
```

**View metrics:**

```bash
curl http://localhost:8080/metrics
```

### Step 4 — Check GitHub

You should see Devin:

- Create a session
- Open a pull request
- Comment on the issue

That completes the end-to-end automation.

---

## API Reference

| Method | Endpoint     | Description                              |
|--------|--------------|------------------------------------------|
| `POST` | `/scan-now`  | Manually trigger an issue scan           |
| `GET`  | `/tasks`     | List all tracked remediation tasks       |
| `GET`  | `/metrics`   | View aggregate metrics across runs       |

### `POST /scan-now`

Triggers an immediate scan of labeled GitHub issues.

**Request:**

```bash
curl -X POST http://localhost:8080/scan-now
```

**Response:**

```text
Scan completed
```

### `GET /tasks`

Returns every tracked remediation task, keyed by issue number.

**Request:**

```bash
curl http://localhost:8080/tasks
```

**Response:**

```json
[
  {
    "taskId": "8f3a1d2e-7b6c-4f5a-9e1d-2c3b4a5d6e7f",
    "issueNumber": 42,
    "issueUrl": "https://github.com/your-username/superset/issues/42",
    "title": "Improve clarity of backend error message",
    "devinResponse": "{\"session_id\":\"devin-abc123\",\"url\":\"https://app.devin.ai/sessions/abc123\"}",
    "status": "SUCCESS",
    "createdAt": "2026-05-03T14:22:18.431Z",
    "error": null
  }
]
```

On failure, `devinResponse` is `null` and `error` contains the failure reason.

### `GET /metrics`

Returns aggregate counts and the latest scan timestamp.

**Request:**

```bash
curl http://localhost:8080/metrics
```

**Response:**

```json
{
  "total_tasks": 5,
  "successful_devin_sessions": 4,
  "failed_devin_sessions": 1,
  "success_rate_percent": 80.0,
  "automation_trigger": "scheduled GitHub issue scanner",
  "tracked_signal": "Devin session creation",
  "processed_issue_numbers": [12, 17, 23, 38, 42],
  "last_scan_time": "2026-05-03T14:22:18.431Z"
}
```

---

## Metrics

The `/metrics` endpoint exposes counts of remediation outcomes. For now, success and failure are defined narrowly around the Devin handoff:

| Outcome   | Definition |
|-----------|------------|
| **Success** | A Devin session was created for the issue |
| **Failure** | No Devin session was created (e.g. API error, invalid issue, network failure) |

> **Note:** These definitions cover only the *handoff* to Devin — they do not yet reflect downstream outcomes such as whether Devin opened a PR, whether the PR was merged, or whether the underlying issue was actually resolved. Richer outcome tracking is planned.
