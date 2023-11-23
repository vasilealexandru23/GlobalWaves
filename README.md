Copyright 2022 Vasile Alexandru-Gabriel (alexandru.vasile03@stud.acs.upb.ro)

# GlobalWaves - Audio Player - Project I

## Structure Of The Project

  * `searchbar/` - Contains implementation for SearchBar commands (search track && select track)
    * `Filters` - Contains all possible filters for a song, podcast or playlist.
    * `SearchCommand` - Contains the type of track and given filters by user and computes the search.
      * `Search Song` - Subclass which contains the implementation for searching a song with given filters.
      * `SearchPlaylist` - Subclass which contains the implementation for searching a playlist with given filters.
      * `Search Podcast` - Subclass which contains the implementation for searching a podcast with given filters.
  * `mydata/` - Contains custom classes with specific fields.
    * `InputData` - Class that contains all fields a command can have, used for reading commands by users.
    * `EpisodeData` - Class that contains data for an episode(Name, Duration, Description) and when it was paused.
    * `PodcastData` - Class that contains data for a podcast(Name, Owner Episodes) and last episode viewed.
  * `musicplayer/` - Contains the implementation for our player. 
    * `MusicPlayer` - Class that contains data of user's player (username, last search, playlists, etc.).
    * `Playback` - Class that contains the implementation for our running playback in player.
    * `Playlist` - Class that contains data and coresponding commands for a playlist.


## Program Flow

Execution starts in `Main.java`, where the commands are read using `InputData.java` class as an ArrayList.

Before iterating through the commands, we first create the custom `PodcastData.java` ArrayList of podcasts,   
and for each user it's coresponding MusicPlayer (`MusicPlayer.java`) in an ArrayList. Also, we create  
a new ArrayList of `Playlists`, where we keep track of all created playlists (public or private).

We start iterating through commands, and for each, we find the coresponding MusicPlayer for the user who  
requested the command. Also for output, we need a new `ArrayNode` used for command status. For each command   
name, we call the coresponding functions and output a message and in success case, the result(s).

## Commands

* `search` : First, unload if their is a loaded track. Than search with given filters the tracks   
             by the given type (there are 2 subclass that extends the `SearchCommand` for easily handle the cases).

* `select` : First, check if there was a search done before. Than output the name, and mark the select parameter.

* `load` :  Start by checking if we have a previous select request. In case we can proceed  
            the command, for the current player, we create a new playback and set it's current track.   
            Then, we mark it's last selection and search as null.

* `status` : First, we check our user's playback status, to refresh and output it's data.

* `playPause` : First, we check our user's playback status, to refresh data, and set/reset it's   
                status (paused or resumed).

* `createPlaylist` : Before creating a new playlist, we check if there is another one with same name.  
                     Then, we create a new playlist with coresponding data and add it to our ArrayList of playlist created.

* `addRemoveInPlaylist` : Start by checking if a song is running in playback. Than, we get the   
                          coresponding playlist and add the current track in it, and ouput a success message.

* `like` : Start by checking if a song or a playlist is running in playback. Than, we get the current   
           playing song and add it to our likedSongs ArrayList in the coresponding music player running.

* `showPlaylists` : Start by iterating over all created playlists by user, and output                             
                    for each the requested data (name, songs, visibility, followers).

* `showPreferredSongs` : We iterate over all current player's liked songs and output their names.

* `follow` :  First, check if their is a running playlist on playback. Than, check if the current    
              running player already has followed the current playlist and increase/decrease # of followers.

* `getTop5Songs` : For this command, we start by iterating over all songs from library, and for each,    
                   we iterate over all players and keep track of likes in a `HashMap`. Also, we add the songs in an    
                   auxiliary ArrayList, that will be sorted by the number of likes.

* `getTop5Playlists` : We iterate over all playlists created, select only the public ones, and all to and     
                       auxiliary ArrayList of songs. Then we sort them by the # of followers and output the first 5 of them.

* `repeat` : Start by checking the status of current player's playback and than change the repeat parameter.

* `shuffle` : Start by checking the status of current player's playback and than change the shuffle parameter.    
              If shuffle wasn't activated, we create the new shuffle playlist and get the index of current playing song.     
              If shuffle was activated, we update the index of the current playing song in the unshuffled playlist.

* `forward` : First, we check the time remained from current episode. If we have to go to the next episode,   
              just update podcast data and the time watched in current episode. If not, we just add to the 
              time watch the forward time.

* `backward` : First, we check the time remained from current episode. If we have to go to the previous episode,      
              just update podcast data and the time watched in current episode. If not, we just substract to the
              time watch the backward time.

* `next` :  Start, by checking the status of current running playback. Than, for each case we move to the next track.    
            In case of currently playing a song, we just set to the and and unload it.

* `prev` : Start, by checking the status of current running playback. Than, for each case we move to the previous track.     
            In case of currently playing a song, we just set to the and and unload it.

* `switchVisibility` : First, extract the playlist from the ArrayList of created playlists by current user
                       and change it's visibility parameter.

**NOTE: For more details about implementation check JavaDoc.**
