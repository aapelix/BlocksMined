## A Fabric 1.21 server-side mod that tracks every mined block and updates a scoreboard with the player's score based on their mining activity.

![Preview of the mod in game](https://cdn.modrinth.com/data/cached_images/bebcb15641e9386ea4fe7cd20f8de0985fe3a719.png)

Any feedback/improvement ideas/bugs can be sent to `hello@aapelix.dev` or you can create an issue of it on Github

All the commands below support minecraft's command arguments suggestions for help

### To set the display location for the scoreboard:
```
/bm display <display position>
```

```
/bm display sidebar
/bm display list
/bm display belowname
```

### Changing the color of the scores in the scoreboard:
```
/bm color <color>
```

Currently the **only supported colors are red and yellow** due to minecraft's limitations

### Change the display name of the scoreboard:

```
/bm display "<name>"
```

```
/bm display "Test name"
/bm display "Blocks Mined"
```

**The name has to be in quotation marks to work**

### To modify the scoreboard scores:

```
/bm addscore <[player]> amount
/bm removescore <[player]> amount
```

```
/bm addscore aapelix 100
/bm addscore @a 99
/bm addscore @p 150
/bm removescore aapelix 100
```

to reset all scores:

```
/bm reset
```

You can use minecraft's built in commands to do stuff with the scoreboard but these commands are much easier to use
# Happy mining!

### Why did I make this?

I've been looking for a mod to track these things on the SMP server that my friends and I play on, but everything I found was outdated or not working, so I decided to make one myself.

### New features coming soonTM:
- Tracking per block, tool etc...