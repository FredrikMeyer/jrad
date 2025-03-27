hello:
    echo "hei"

test:
    ./mvnw test

pitest:
    ./mvnw test-compile org.pitest:pitest-maven:mutationCoverage -DexcludedTestClasses=net.fredrikmeyer.jisp.repl.GUIExampleTest

mutation-coverage: pitest
    open ./target/pit-reports/index.html

build:
    ./mvnw package

repl:
     ./mvnw -DskipTests=true exec:java -Dexec.mainClass=net.fredrikmeyer.jisp.Main
