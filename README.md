# jenkins-fail-on-reduced-tests-plugin 
[![Build Status](https://travis-ci.org/nurk/jenkins-fail-on-reduced-tests-plugin.svg?branch=master)](https://travis-ci.org/nurk/jenkins-fail-on-reduced-tests-plugin) [![Coverage Status](https://coveralls.io/repos/github/nurk/jenkins-fail-on-reduced-tests-plugin/badge.svg?branch=master)](https://coveralls.io/github/nurk/jenkins-fail-on-reduced-tests-plugin?branch=master)

This plugin is a post build action plugin.

It allows you to configure a percentage that the amount of unit tests are allowed to reduce.

If no previous successful build is found it will fallback on the minimum amount of expected unit tests, which also needs to be configured.

It will mark the build as unstable if the amount of unit tests does not meet the set criteria which will help protect your project from configuration mistakes.
(It considers skipped tests as tests that have not run)


## License

Licensed under MIT