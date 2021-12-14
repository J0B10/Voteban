<p>
<img align="right" src="https://raw.githubusercontent.com/joblo2213/Voteban/master/voteban.png">
<img align="right" width="128" src="https://cdn.discordapp.com/attachments/449265416183742465/594175597039714337/hack_wump.png">
</p>

# Voteban

**Disclaimer:** _This README and the associated bot is one big sarcastic shitpost.  
No one will ever be banned or kicked by the bot.  
We believe that talking to each other and explaining misbehaviour is better than just voting to ban people.  
`/voteban` is a meme that now has been used in our community for a long time to show disagreement but at the same time have a great laugh all together.  
If you are looking for a real moderation tool this is propably not the right bot.  
Also be aware if using the bot that it sadly has the potential to be misused or misunderstood._  


### ⚠️ This bot has reached EOL and was affected by [`CVE-2021-44228`](https://github.com/advisories/GHSA-jfh8-c2jp-5v3q). Do not run it!

--------------------

There are so meany reasons why you would need `/voteban`:

<img align="right" src="https://raw.githubusercontent.com/joblo2213/Voteban/master/memes/everyone_gets_a_ban_operah.jpg">

Trojaner posts screenshots with light theme - **`/voteban trojaner`!**

MelanX is telling incredibly bad jokes - **`/voteban melan`!**

kegelsknight is trolling again - **`/voteban kegelsknight`!**

skate702 is piling up open TODOs - **`/voteban skate702`!**

felixletsplay is using eclipse - **`/voteban felixletsplay`!**

ungefroren isn't merging my PR - **`/voteban ungefroren`!**

derNiklaas is to blame - **`/voteban derNiklaas`!**

Sireisenblut is existing - **`/voteban Sireisenblut`!**

**Everybody loves to ban! Start banning your friends now!**

## Usage
```
/voteban <username> <optional ban message>
```
Vote that a user should get banned. A really satisfying command.  
You can add a ban message or leave it up to the bot to select a fitting one.  

------------------------
```
/mybans
```
Displays your stats. How many users voted to ban you and how often you voted to ban someone.  

------------------------
```
/whobanned
```
Shows the headhunter who banned most users.

------------------------
```
/mostbanned
```
Shows who are the most banned users on the server,

------------------------

## Installing & customizing

You can just [invite the bot](https://discordapp.com/api/oauth2/authorize?client_id=593953207420715019&permissions=84992&scope=bot) or download the [latest release](https://github.com/joblo2213/Voteban/releases) and run it yourself.  
[Here](https://github.com/reactiflux/discord-irc/wiki/Creating-a-discord-bot-&-getting-a-token) is a guide how to get an api token for the bot.  

No matter if you are self hosting the bot or just invited him using the link you can upload your own config file to customize the bot:  
Just send him `config?` as a private message and he will answer with your servers current configuration.  
Just edit the file and send it back to the bot.  
The file is pretty much self explaining, you can add/remove ban messages and images or customize how man users are shown in the leaderboards.  
Maybe there will be more customization in the future. 

## Related stuff

The bot was created during the [discord hack week](https://www.google.com/url?sa=t&rct=j&q=&esrc=s&source=web&cd=2&cad=rja&uact=8&ved=2ahUKEwjfirqNmI3jAhUKKewKHQDHCakQFjABegQIAhAB&url=https%3A%2F%2Fblog.discordapp.com%2Fdiscord-community-hack-week-build-and-create-alongside-us-6b2a7b7bba33&usg=AOvVaw31LDi7adDsNHfcGT9U-UiI).

We used [log4j](https://logging.apache.org/log4j/2.x/), [JDA](https://github.com/DV8FromTheWorld/JDA), [scala-xml](https://github.com/scala/scala-xml) and [json4s](https://github.com/json4s/json4s) for creating this bot. All those libraries are licensed under [Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0)

This bot is licensed under [EPL 2.0](https://github.com/joblo2213/Voteban/blob/master/LICENSE).

Discord users involved in this project: `derNiklaas#6011`, `Sireisenblut#1813`, `ungefroren#2222`.

Also a big thank you to all members from the [skate702](http://skate702.de/) subscriber discord server that suggested ban messages and memes! 
