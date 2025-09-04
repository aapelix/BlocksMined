## A Fabric mod that tracks every mined and placed block

![Preview of the mod in game](https://cdn.modrinth.com/data/cached_images/bebcb15641e9386ea4fe7cd20f8de0985fe3a719.png)

Any feedback/improvement ideas/bugs can be sent to `hello@aapelix.dev` or you can create an issue of it on Github

## Commands:

### To set the display location for the scoreboard:
```
/blocks <mined | placed> display <display position>
```

```
/blocks <mined | placed> display sidebar
/blocks <mined | placed> display list
/blocks <mined | placed> display belowname
```

### Changing the color of the scores in the scoreboard:
```
/blocks <mined | placed> color <color>
```

Currently, the **only supported colors are red and yellow** due to minecraft's limitations

### Change the display name of the scoreboard:

```
/blocks <mined | placed> display "<name>"
```

```
/blocks <mined | placed> display "Test name"
/blocks <mined | placed> display "Not Blocks Mined"
```

**The name has to be in quotation marks to work**

### To modify the scoreboard scores:

```
/blocks <mined | placed> addscore <[player]> amount
/blocks <mined | placed> removescore <[player]> amount
```

```
/blocks <mined | placed> addscore aapelix 100
/blocks <mined | placed> addscore @a 99
/blocks <mined | placed> addscore @p 150
/blocks <mined | placed> removescore aapelix 100
```

to reset all scores:

```
/blocks <mined | placed> reset
```

You can use minecraft's built in commands to do stuff with the scoreboard but these commands are much easier to use
# Happy mining!

### Why did I make this?

I've been looking for a mod to track these things on the SMP server that my friends and I play on, but everything I found was outdated or not working, so I decided to make one myself.

### New features coming soon (hopefully):
- Tracking per block, tool...