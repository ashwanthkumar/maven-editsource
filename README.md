# maven-editsource
Maven EditSource Plugin.

## Usage
```xml
<plugin>
    <groupId>in.ashwanthkumar</groupId>
    <artifactId>maven-editsource</artifactId>
    <version>0.0.2</version>
    <executions>
        <execution>
            <phase>generate-resources</phase>
            <goals>
                <goal>edit</goal>
            </goals>
            <configuration>
                <variables>
                    <version>${project.version}</version>
                </variables>
                <files>
                    <file>
                        <input>${project.basedir}/src/main/resources/plugin.xml</input>
                        <output>${project.build.outputDirectory}/plugin.xml</output>
                    </file>
                </files>
            </configuration>
        </execution>
    </executions>
</plugin>
```

## License

http://www.apache.org/licenses/LICENSE-2.0
