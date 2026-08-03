---
description: Build the project with Maven (mvnw clean install), skipping tests by default
---

Run the project build using the Maven wrapper from the repository root:

```
./mvnw clean install -DskipTests
```

On Windows PowerShell, use `.\mvnw.cmd clean install -DskipTests` instead.

If the user passed arguments via $ARGUMENTS, append them to the command instead of `-DskipTests` (e.g. to run with tests, or a different goal).

Report only the build result (success/failure) and, on failure, the first compiler/build error — not the full log.
