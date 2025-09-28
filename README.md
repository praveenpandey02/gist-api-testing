# Case Study: Distribusion Technologies
This repository is a case study on a brief API testing of Github's Gist functionality.
## Important points to take note of:
1. Most of the tests are on public gists and not secret gists. This is done for brevity
2. Rest Assured, a Java library is used to test these APIs
3. JUnit 5 is used as the framework
4. For the sake of simplicity, tests are eith tagged as smoke or regression or not tagged at all
5. Smoke tagged tests run automatically for any pull request raised against the main branch
6. Regression tagged tests are triggered when the pull request is ready and is merged to the main branch
