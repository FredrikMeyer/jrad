hello:
    echo "hei"

test:
    mvn test

pitest:
    mvn test-compile org.pitest:pitest-maven:mutationCoverage

mutation-coverage: pitest
    open ./target/pit-reports/index.html

build:
    mvn compile

repl:
     mvn -DskipTests=true clean package exec:java -Dexec.mainClass=net.fredrikmeyer.jisp.Main
