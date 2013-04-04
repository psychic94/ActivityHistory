This plugin allows you to monitor who is active and when. If you also use Vault, it will log the permission group ratio. It will help determine who to make staff by supplying you with reliable information about who is often online when staff is needed. It has a variety of log query commands to interpret the information stored.(in development)

News

    SQL mode in development!
    Having a banner contest. Submit a ticket with the banner. If I like it, it will become the official banner and you will be a contributor.

Upcoming Features:

    Optional SQL support
    Web app to show graphs of the data collected
    Fix ahgroup
    Ability to ignore afk players using essentials
    Ability to autokick inactive players from factions.
    More configurables

Current Commands:

    /ahplayer <player> [<date/time> [to <date/time>]] [at <hour>] - checks record to and reports the percent of time the player was online in the period specified; the at param restricts the check to a the time of day specified.
    /ahgroup staffdist [start [end]] - draws a histogram illustrating the average staff percent during each hour of the day. Also shows the average peak. Currently not working properly.

Upcoming Commands:

    /ah <player> online <date/time> [within <minutes>] - checks records if player was online during the period specified; the within param defaults to the survey closest to the time specified
    /ah <player> online between <date/time> <date/time> - checks records if player was online during the period specified
    /ah <grouppercent|grppcnt|gp> at <time> since <date> - returns the average distribution of permissions groups online at the time of day specified
    /ah <grouppercent|grppcnt|gp> at <time> between <date> <date> - returns the average distribution of permissions groups online at the time of day specified
    /ah clean before <date/time> - erases data before the time specified

Notes:

    params in <> are required, params in [] are optional
    <time> uses format hh:mm:ss, <date> uses format MM.DD.YY, <date/time> uses format MM.DD.YY-hh:mm:ss, <hour> is an integer from 0 to 23.
    grouppercent commands require Vault

Permissions:

    activityhistory.query.player - allows checking of player activity
    activiyhistory.query.group - allows checking of group distribution
    activityhistory.admin - includes all of the above plus log cleaning
