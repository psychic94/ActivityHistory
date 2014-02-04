This plugin allows you to monitor who is active and when. If you also use Vault, it will log the permission group ratio. It will help determine who to make staff by supplying you with reliable information about who is often online when staff is needed. It has a variety of log query commands to interpret the information stored.(in development)

News

    SQL mode released! Group queries for SQL still being developed.
    Localization feature added. If you would like to contribute a language, translate the en-us.yml file in the jar and submit it in a ticket.
    Having a banner contest. Submit a ticket with the banner. If I like it, it will become the official banner and you will be a contributor.

Upcoming Features:

    Further develop group queries
    Continual mode
    Web app to show graphs of the data collected
    Ability to ignore afk players using essentials
    Ability to autokick inactive players from factions.
    More configurables

Current Commands:

    /ppercent <player> [start [end]] ["at" hour]: Shows the percent of online time between <start> and <end>. Restrict the search to a certain time using <hour>.
    /ptotal <player> [start [end]]: Shows the total online time between <start> and <end>.
    /phours <player> [start [end]]: Shows the percent of online time between <start> and <end> by hour.

Upcoming Commands:

    /online <player> <date/time> ["within" minutes] - checks records if player was online during the period specified; the within param defaults to the survey interval
    /online <player> "between" <date/time> <date/time> - checks records if player was online during the period specified
    /gpercent <group> [start [end]] ["at" hour]: Shows the percent of online time between <start> and <end>. Restrict the search to a certain time using <hour>.
    /staffdist [start [end]] - Shows the average percent of players that are staff during each hour of the day.
    /ahclean <date/time> - erases data from before the time specified
    /ahdump <date/time> - saves data from before the time specified to a flat file and removes it from the database
    /ahbackup <date/time> - saves data from before the time specified to a flat file without deleting it from the database
    /ahrestore <date/time> - adds data from before the time specified from flat files to the database

Notes:

    params in <> are required, params in [] are optional, params in "" should be typed word for word
    <time> uses format hh:mm:ss, <date> uses format MM.DD.YY, <date/time> uses format MM.DD.YY-hh:mm:ss, <hour> is an integer from 0 to 23.
    Example timestamps: 1/1/00-0:30:00, 3/24-5:10, 5/12-4, 3-18:20
    Group queries require Vault

Permissions:

    activityhistory.query.player - allows checking of player activity
    activiyhistory.query.group - allows checking of group distribution
    activityhistory.admin - includes all of the above plus log cleaning
