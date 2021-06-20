# Telephone

#### "Like [jooq](https://www.jooq.org/) but for game networking"
#### A code generation tool which converts a JSON configuration into a network parsing library.

# !!!WARNING!!!
#### This was architected in <24 hours and definitely needs work (and documentation) before it is used in any kind of workflow automatically.


# Usage
#### Telephone is intended to be added to the project root and called through a maven build script, ie:
```
<build>
    ...
    <plugins>
        ...
        <plugin>
            <groupId>org.codehaus.mojo</groupId>
            <artifactId>exec-maven-plugin</artifactId>
            <executions>
                <execution>
                    <id>Telephone</id>
                    <phase>generate-sources</phase>
                    <goals>
                        <goal>java</goal>
                    </goals>
                </execution>
            </executions>
            <configuration>
                <executable>java</executable>
                <arguments>
                    <argument>-jar</argument>
                    <argument>Telephone-0.2.jar</argument>
                </arguments>
            </configuration>
        </plugin>
    </plugins>
</build>

```
That said, the principle author manually calls the jar for the meantime.

# Configuration
#### [An example configuration file is provided in the root directory.](https://github.com/AustinHoover/SocketMessageCodeGen/blob/master/template.json)

# Building
#### Build the project with maven
```
mvn package
```
#### The resultant jar needs to be added to the project root directory.