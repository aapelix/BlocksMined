### A Fabric 1.21 server-side mod that tracks every mined block and updates a scoreboard with the player's score based on their mining activity.

![Preview of the mod in game](https://cdn.modrinth.com/data/cached_images/bebcb15641e9386ea4fe7cd20f8de0985fe3a719.png)

The name of the scoreboard is ´blocksMined´

To display the scoreboard on sidebar use the following command:
```
/scoreboard objectives setdisplay sidebar blocksMined
```

If you want you can change the `sidebar` to like `list` or `below_name` to display the scoreboard somewhere else

To modify the scoreboard (I mean like clear scores, add scores, change display name) you can just use Minecraft's own built in commands like:


```
/scoreboard players add aapelix blocksMined 100
/scoreboard players reset @a blocksMined
```

### Why did I make this?

I've been looking for a mod to track these things on the SMP server that my friends and I play on, but everything I found was outdated or not working, so I decided to make one myself.
