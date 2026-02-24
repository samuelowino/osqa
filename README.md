<img src="app_icon.png" width="100" />

# OSQA 
> Pronounced oska

On-Device Software Quality Assurance (OSQA). Software can and should be deterministic 

## How it work

- Load a software expected behaviour specification with verification steps
- Launch a quality assurance session
- Verify expected behaviour
- Analysis quality assurance session results and analysis

## Configuration Options

- Minimum passed test cases
- Minimum passed test cases per module

OSQA is not persistent, data is only collected and mantained during a QA session, when the QA session is terminated, all data is deleted.

## Sample Test Case Specification

```json
{
  "test_cases": [
    {
      "id": "TC-PRE-001",
      "title": "Pre-Test Setup and Calendar Launch",
      "category": "Setup",
      "priority": "High",
      "user_action": "Launch the application and navigate to the In-App Custom Calendar view (Month/Week view). Ensure the system date is set to a known baseline (e.g., Monday, January 1st, 2024).",
      "verifications": [
        "App loads successfully with no crashes or error messages.",
        "The custom calendar renders correctly, showing the correct month/week and dates."
      ],
      "depends_on": []
    },
    {
      "id": "TC-CAL-001",
      "title": "Daily Recurrence - Visual Placement on Calendar",
      "category": "Recurrence - Daily",
      "priority": "High",
      "user_action": "Create a new task titled 'Hydrate' and set its recurrence to 'Daily,' starting on the current date (Jan 1, 2024). Then, open the In-App Calendar to the Month view for January 2024.",
      "verifications": [
        "The task is created successfully and displays a recurrence icon.",
        "In the In-App Calendar, a task indicator for 'Hydrate' appears on January 1st.",
        "Scroll through the month view. Verify that 'Hydrate' appears on every single day in January (Jan 2, Jan 3, Jan 4... Jan 31)."
      ],
      "depends_on": ["TC-PRE-001"]
    }, ...
  ]
}
```
