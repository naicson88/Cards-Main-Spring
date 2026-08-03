---
name: run-tests
description: Use this skill to run the project's Maven test suite (via the mvnw wrapper) and summarize failures. Triggers on requests like "run the tests", "rode os testes", "mvn test".
---

Run the test suite using the project's Maven wrapper from the repository root:

```
./mvnw test
```

On Windows PowerShell, use `.\mvnw.cmd test` instead.

After the run:
- If it passes, report the number of tests run and skip further detail.
- If it fails, identify the failing test class(es) from the Surefire summary, open the relevant test file(s) under `src/test/java`, and summarize the assertion failure(s) — do not paste the full stack trace unless asked.
- Do not attempt to fix the failure automatically unless the user asks for a fix.
