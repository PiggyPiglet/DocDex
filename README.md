# ![DocDex](https://cdn.piggypiglet.me/docdex/banner.png)
[<img src="https://img.shields.io/jenkins/build?jobUrl=https%3A%2F%2Fci.piggypiglet.me%2Fjob%2FDocDex">](https://ci.piggypiglet.me/job/DocDex) [<img src="https://img.shields.io/discord/164280494874165248">](https://helpch.at/discord)

Documentation Index (DocDex) is a utility API which scans javadocs on the web, caches everything into a calculated mixture of memory, file, and database, then provides a JSON API to easily access the information. Warning, don't run this on remote sites, it probably won't work, and you probably won't be able to access that site afterwards.

## Compilation
```bash
./gradlew shadowJar
```
> Archives will be in app/build/libs/ and discord/build/libs/

## Usage
Requirements:
- Java 11
- MongoDB

### API App
```bash
java -Xmx2G -jar docdex.jar
```
Make sure to populate the config.json with your javadocs. On first start, it'll crawl the sites (which can take a while), and then save the indexes to file & database. As mentioned earlier, you should only run this on local sites. Doing otherwise will result in extremely long crawl times, or your ip being banned from said site. Multiple javadoc sites are loaded concurrently to help with speed.

The idea is that after a crawl, the index will be saved into a file (json). This allows the index to easily be moved around. There's two population methods in DocDex, crawl, which scans the web javadocs, and flatfile, which loads from the json file(s). Flatfile is much faster than crawl, so you should always use prebuilt indexes if possible.

Once population has finished, there's two storage methods. DocDex will attempt to save the loaded objects into a flatfile (if one doesn't already exist). It'll then load them into the database (once again if the collection doesn't already exist). The database is where the actual index gets its data from, not the flatfile.

When running the jar, a web server will be spun up on the port & host specified in the config. The route `/index` will then be made available. Two parameters are required to receive a non-null response from this route, javadoc and query. Javadoc refers to one of the names inside your config.json, and the query is one of the objects from the javadoc (e.g. a class or method).

Both methods (methods, constructors) and types (classes, interfaces, enums, annotations) have two identifiers. The first is their name, e.g. `CommandExecutor` (name) or `CommandExecutor#onCommand` (method). Additionally, there's also FQN identifiers in the event of duplicate names, e.g. `org.bukkit.command.CommandExecutor` & `org.bukkit.command.CommandExecutor#onCommand`.

#### Routes
When requesting from the index with a query, it'll first attempt to find a perfect match for the query (two possible matches, name or fqn). If it can't find one, it'll use a ratio of the levenshtein distance of the algorithm, and either the name or fqn (determined by whether the query contains periods) of every object in that javadoc. e.g.
```
/index?javadoc=1.16.4&query=commandexecutor
/index?javadoc=1.16.4&query=commandexecutor~oncommand
/index?javadoc=jdk&query=map~getordefault(key, defaultvalue)
/index?javadoc=1.16.4&query=material-birch_log
```
> - Queries are case insensitive.<br/>
> - Use ~ instead of # for methods (including constructors)<br/>
> - Parameters for methods are optional. If you include them, you can either use
> parameter names, parameter types, or the entire thing. (e.g. `key`, `string`, or `string key`)
> - Use - instead of % for fields (including enum constants)

There's also an optional limit parameter you can add on, which will limit the amount of results you get for non-exact queries (i.e. searches). The default value is 5.
```
/index?javadoc=1.16.4&query=playah&limit=3
```

In addition to the index, a second endpoint is exposed `/javadocs`, which provides a list of all the loaded javadocs.

### Discord Bot
You can either use the public instance (which uses https://docdex.helpch.at) or run your own. The public invite is https://piggypiglet.me/docdex.

```
java -Xmx1G -jar bot.jar
```
On first run, a config will generate, and the application will probably spew a few errors. Populate the config with your token, along with your docdex link, and if you wish to limit certain commands to particular channels, you can do so via the config. The default prefix is `d;`, and you should familiarise yourself with the commands which are accessible via `d;help`.

> - Paginations will cease to function after 15 minutes with no activity. i.e. if someone hasn't reacted to a pagination for 15 minutes, the pagination will no longer work.
> - If you set your prefix to something stupid and can't use the bot anymore, simply send `@DocDex resetprefix`. Removing the bot from your server will not remove its saved settings.

#### Commands
The bot currently has 7 commands, all of which can be viewed through the bot via `d;help`. However, heres a more detail description of each:

**d;info**:<br/>
Just shows some basic information about the bot, along with a description. Currently,
it provides a link to the api, github, and discord invite. It also has my name,
the amount of servers its in (and user count), and the javadoc count (along with
the default javadoc). There's also a version number.

**d;docs** / **d;javadocs**:<br/>
This command provides a list of all the javadocs which are accessible via the bot.

**d;** / **d;search**:<br/>
This is the default command, the search command, which allows you
to retrieve javadoc information. It's quite a complicated one, with 3 parameters. They're
explained below, in the next command.

**d;sub_interfaces** / **d;super_interfaces** / **d;extensions** /<br/>
**d;implementing_classes** / **d;all_implementations** / **d;methods** /<br/>
**d;sub_classes** / **d;implementations** / **d;fields**:<br/>
These are effectively all the same command, they just show a different metadata object.
The syntax is identical to the search command, having 3 parameters, which are:
`[javadoc] [limit/$] <query>`. As you can see, the only required parameter is `query`.
The javadoc refers to one of the possible javadoc names, for example `jdk`, or `spigot`.
In the docs menu, javadocs separated with / mean they're aliases. So for example, with 
`spigot/1.16.4`, you could use either `1.16.4` or `spigot` with the search command, or any
of these metadata commands. The limit is a number, which you can use on fuzzy queries. It
dictates how many results are returned. By default, the limit is five. Alternatively, you
can use the symbol `$` for your limit, which will simply return the first result. Here are some
examples:<br/>
- `d;map#getordefault` - returns five suggestions.
- `d;map#getordefault(key, defaultvalue)` - returns information on Map#getOrDefault.
- `d;$ map#getordefault` - returns information on Map#getOrDefault.
- `d;3 map#getordefault` - returns 3 suggestions.
- `d;implementing_classes collection` - returns all implementations of java.util.Collection.
- `d;spigot player` - returns information on spigot's Player interface.
- `d;spigot playah` - returns 5 suggestions, `player` being the first one.
- `d;spigot 3 playah` - returns 3 suggestions, `player` still being the first one.
- `d;spigot $ playah` - returns information on spigot's Player interface.
- `d;spigot material%birch_log` - returns information on spigot's Material.BIRCH_LOG

> If a javadoc object is too big to be viewed in discord, a link will simply be sent to view it
> online.

#### Permission Based Commands
Permission based commands are by default, limited to users with the ADMINISTRATOR permission.
However, you can allow/disallow access to specific commands for role(s) via one of the commands
below.

**d;prefix**:<br/>
This command sets the servers prefix, e.g. `d;prefix d>`. From there on out, you'd use `d>` instead
of `d;`.

**d;role**:<br/>
This command allows you to declare whether a particular role can access permission based
commands. Usage is `d;role <add/remove> <role>`. e.g. `d;role add 322698583377707008`. You can get
a role's id by tagging it, and putting a blackslash before the @ e.g.
![getroleid](https://cdn.piggypiglet.me/docdex/getroleid.gif)

**d;command**:<br/>
This command allows you to edit the "rules" for a command. Commands currently have 3
possible rules, "allow", "disallow", and "recommendation". Allow/disallow refers to
which channels the command can be used in, and the recommendation is a message which will
be sent if a user attempts to use a command in a channel which does not fall into the
constraints you set in allow/disallow. By default, any command can be used anywhere. If
you add an allow, the command can only be used in those channel(s). If you add a disallow,
the command can be used anywhere but the channels you have disallowed. The usage is
`d;command <allow/disallow/recommendation> [add/remove] <command> <value>` For example:
- `d;command allow add help 248724056692621313` - Add `248724056692621313` to `help`'s allowed
channel list.
- `d;command allow remove help 248724056692621313` - Remove `248724056692621313` from `help`'s
allowed channel list.
- `d;command disallow add help 248724056692621313` - Add `248724056692621313` to `help`'s
disallowed channel list.
- `d;command recommendation help This command can only be used in #bot-commands.` - Set
the recommendation for "help" to "This command can only be used in #bot-commands.".

## Contact
Join either [HelpChat](https://helpch.at/discord), or my personal [discord](https://piggypiglet.me/discord).