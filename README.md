<img src="transparent_icon.png" width="250" />

# OSQA 
> Pronounced oska

![Java CI](https://github.com/samuelowino/osqa/workflows/Java%20CI%20with%20Maven/badge.svg)
![Multi-Platform Build](https://github.com/samuelowino/osqa/workflows/Multi-Platform%20Binary%20Generation/badge.svg)

On-Device Software Quality Assurance (OSQA). Software can and should be deterministic 

## How it works

- Load a software expected behaviour specification with verification steps
- Launch a quality assurance session
- Verify expected behaviour per test case
- Evaluate the quality assurance session results

## Configuration Options

- Minimum passed test cases
- Minimum passed test cases per feature

OSQA is not persistent, data is only collected and maintained during a QA session, when the QA session is terminated, all data is deleted.

## Sample Features Specification

```json
{
  "features": [
    {
      "id": "a76b4d46-e7df-43ea-afec-221b899ae527",
      "name": "Core Calendar and Navigation",
      "description": "Validates basic calendar rendering, navigation controls, and fundamental UI elements.",
      "priority": "Critical",
      "test-cases": [
        {
          "id": "TC-SMOKE-001",
          "title": "Smoke - Create Daily Task",
          "spec-file": "tc-smoke-001.json"
        }
      ]
    },
    {
      "id": "9721cac2-bdac-4bbc-85bf-0ee136adbd3b",
      "name": "Basic Recurrence Creation",
      "description": "Tests the fundamental creation of daily, weekly, and yearly recurring tasks and their initial placement on the calendar.",
      "priority": "Critical",
      "test-cases": [
        {
          "id": "TC-SMOKE-001",
          "title": "Smoke - Create Daily Task",
          "spec-file": "tc-smoke-001.json"
        }
      ]
    }
  ]
}
```

## Sample Test Case Specification

```json
{
  "test_cases": [
    {
      "id": "TC-PRE-001",
      "title": "Pre-Test Setup and Calendar Launch",
      "category": "Setup",
      "priority": "High",
      "user_action": "Launch the application and navigate to the In-AppCLI Custom Calendar view (Month/Week view). Ensure the system date is set to a known baseline (e.g., Monday, January 1st, 2024).",
      "verifications": [
        "AppCLI loads successfully with no crashes or error messages.",
        "The custom calendar renders correctly, showing the correct month/week and dates."
      ],
      "depends_on": []
    },
    {
      "id": "TC-CAL-001",
      "title": "Daily Recurrence - Visual Placement on Calendar",
      "category": "Recurrence - Daily",
      "priority": "High",
      "user_action": "Create a new task titled 'Hydrate' and set its recurrence to 'Daily,' starting on the current date (Jan 1, 2024). Then, open the In-AppCLI Calendar to the Month view for January 2024.",
      "verifications": [
        "The task is created successfully and displays a recurrence icon.",
        "In the In-AppCLI Calendar, a task indicator for 'Hydrate' appears on January 1st.",
        "Scroll through the month view. Verify that 'Hydrate' appears on every single day in January (Jan 2, Jan 3, Jan 4... Jan 31)."
      ],
      "depends_on": ["TC-PRE-001"]
    }, ...
  ]
}
```

---

## How to run the app

### Prerequisites
- Java 25 or later

### Option 1: Run the fat jar
Download the latest `osqa-1.3.jar` from the releases page and run:
```bash
$ java -jar osqa-1.3.jar
```
The fat jar bundles all dependencies including JavaFX for Windows, Linux, and Mac — only Java is required.

### Option 2: Build from source
```bash 
$ git clone git@github.com:samuelowino/osqa.git
$ cd osqa
$ mvn clean package
$ java -jar target/osqa-1.3.jar
```

### OR just execute ./gui.sh

```bash
chmod +x gui.sh
./gui.sh
```


<!-- coverage start -->
## 📊 Code Coverage Report

**Overall Coverage: 39.40% ⚠️**

| Metric      | Covered | Missed | Total | Coverage  |
|-------------|---------|--------|-------|-----------|
| INSTRUCTION | 961     | 1478   | 2439  | 39.40% ⚠️ |
| LINE        | 211     | 311    | 522   | 40.42% ⚠️ |
| BRANCH      | 74      | 94     | 168   | 44.05% ⚠️ |
| METHOD      | 30      | 40     | 70    | 42.86% ⚠️ |
| CLASS       | 13      | 9      | 22    | 59.09% ✅  |
| COMPLEXITY  | 42      | 113    | 155   | 27.10% ⚠️ |

### 🚨 Least Tested Elements (coverage below 50%)
- INSTRUCTION: 39.40%
- LINE: 40.42%
- BRANCH: 44.05%
- METHOD: 42.86%
- COMPLEXITY: 27.10%
<!-- coverage end -->
