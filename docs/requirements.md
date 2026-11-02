# FIAP requirements traceability

## Source status

`docs/ADJT - Fase 4 - Tech Challenge_rev.pdf` is preserved but could not be extracted in this environment. This baseline uses the complete FIAP requirements supplied in the project request.

## Mandatory functional requirements

| FIAP requirement | Implementation evidence |
| --- | --- |
| HTTP student evaluation submission | `POST /avaliacao` in `feedback-api`; ASCII spelling is documented. |
| Validate description and grade 0-10 | `FeedbackService` and unit tests. |
| Store evaluations | DynamoDB table defined in `template.yaml`. |
| Identify and notify critical feedback | Central urgency classifier, DynamoDB Streams Lambda and SNS. |
| Weekly report with average, details, daily and urgency counts | `WeeklyReportService`, scheduled report Lambda and SNS. |
| Date, description and urgency in outputs | Notification and report formatting. |

## Mandatory nonfunctional requirements

| FIAP requirement | Status |
| --- | --- |
| Cloud serverless and at least two separate functions | Three separately packaged Lambda artifacts and SAM resources: locally implemented; AWS validation required. |
| Supporting infrastructure, security/governance, monitoring | SAM includes API Gateway, DynamoDB, SNS, scheduler, least-privilege policies, logs and error alarms; AWS validation required. |
| Automated deployment | GitHub build and manual OIDC deployment workflows. |
| Documentation | README, SAM template and this traceability document. |

## Deliverables and evaluation

The public repository and demonstration video require owner action after AWS deployment. The video must show functions, cloud configuration and working API/notifications/report. Cloud delivery is not claimed verified without deployment.

## Selected stack (implementation decision)

Java 21, Quarkus 3, Maven, AWS Lambda/API Gateway/DynamoDB Streams/SNS/EventBridge Scheduler/CloudWatch, AWS SAM, GitHub Actions, JUnit 5 and Mockito.

## Business rules (implementation decisions)

- Grades 0-3 are CRITICAL, 4-6 MEDIUM and 7-10 LOW.
- UTC timestamps are stored; reports use `America/Sao_Paulo`, prior Monday-to-Monday week, exclusive end.
- `ReportWeekIndex`, keyed by Monday date, avoids a report-time table scan.
- Stream processing is at-least-once. An in-memory event-id guard reduces warm-instance duplicates; strict cross-instance notification deduplication is intentionally out of scope. SNS errors are rethrown for Lambda retry.

## Optional improvements

API authorization, persistent notification idempotency, dead-letter queues, S3 report archive, dashboards and alarm actions are optional, not FIAP requirements implemented here.

## Checklist

- [x] Three independent function responsibilities and local unit tests
- [x] SAM topology, monitoring configuration and manual deployment workflow
- [ ] AWS deployment/SNS/email validation
- [ ] Public repository and demonstration video
