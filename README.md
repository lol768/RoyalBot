RoyalBot
========

lol768's IRC bot, modified by Bionicrm (created by RoyalDev)

Plugins
-------

RoyalBot has a plugin system that any developer can easily hook into. Please see
[GithubPlugin](https://github.com/jkcclemens/GithubPlugin) for an example of a RoyalBot plugin.

Be sure to use Maven dependencies:
```xml
<repositories>
    <repository>
        <id>royaldev-repo-snap</id>
        <url>http://minor.royaldev.org:8081/nexus/content/repositories/products-snap/</url>
    </repository>
    ...
</repositories>
<dependencies>
    <dependency>
        <groupId>org.royaldev</groupId>
        <artifactId>royalbot</artifactId>
        <version>LATEST</version>
    </dependency>
    ...
</dependencies>
```
