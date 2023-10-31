
ifeq ($(OS), Windows_NT)
  MVN = ./mvnw.cmd
  OPEN = start
else
  MVN = ./mvnw
  OPEN = open
endif

# run quarkus in development mode
qd:
	${MVN} quarkus:dev

# apply codeformatting
codeformat:
	${MVN} spotless:apply

# build and package the application
unit-test-ci:
	./mvnw verify -Dquarkus.package.type=uber-jar -Dquarkus.package.add-runner-suffix=false

unit-test: codeformat detekt unit-test-ci

# package the application
package:
	${MVN} -B clean deploy -Dquarkus.package.type=uber-jar -Dquarkus.package.add-runner-suffix=false

# Code quality checks
detekt:
	${MVN} detekt:check

# Clean the project for working files
clean:
	${MVN} clean
