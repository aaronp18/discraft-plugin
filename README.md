<h1>discraft-plugin</h1>
<h4>The plugin required for Discraft</h4>
<hr>
<h5>About</h5>
Discraft is a bot/plugin that enables discord users to run commands from discord to their Minecraft Server. It is all a work in progress. If you find any issues please file a bug report, thank you :P

Use `d!setup` for information on how to setup the bot in discord or `d!help` for command help.

<h5>Setup</h5>
<hr>
Firstly, download and install the Discraft plugin from <a href="https://github.com/aaronp18/discraft-plugin/releases"> releases </a> into your plugins folder on your Minecraft server.

Then invite the bot <a href="https://discord.com/oauth2/authorize?client_id=714564857822969868&scope=bot&permissions=150528">Click Here</a> to your server.

Then do `d!auth` to get the authkey for your server. This will be sent to you via DM, keep this secure!

Then once you've started the minecraft server copy the `authkey` from the DM either into the `authkey` field in the config.yml within `plugins\discraft` or into `/discraft auth <authkey>`.

Then in the minecraft server, run `/discraft link` to link the minecraft server to the discord bot.

Everything should be setup now

<hr>
<h5>Usage</h5>
<h6>Whitelisting</h6>
One advantage of this bot is that you can make a role that will allow discord users with that role to add themselves to the whitelist automatically.

Firstly make a role that you want to be required in order to add to the whitelist.

Then do `d!whitelist role set @Role` to set the role.

Players should then be able to link their Discord with their Minecraft username using `d!whitelist link <Minecraft Name>`.

And then use `d!whitelist me` to add themselves to the whitelist.",

<h6>Running Commands</h6>

Using `d!run <command`, admins should be able to execute commands from the discord. This is still a WIP though

<hr>
<h5>Troubleshooting</h5>
<h6>The plugin doesn't start correctly</h6>
This can happen for a couple of reasons.

One major one is that the port it is trying to listen to is being used by something else.

To fix this, simply use `/discraft port <new port>` or change the `config.yml` `port` field of the minecraft server and restart.

Then use `d!port <new port>` in the discord to save it to the server.

This should fix it. If not then send an email to `support@hourglassprograms.com`
