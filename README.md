Copyright 2022 Vasile Alexandru-Gabriel (alexandru.vasile03@stud.acs.upb.ro)

# GlobalWaves - Audio Player - Project I

## Structure Of The Project

  * `searchbar/` - Contains implementation for SearchBar commands (search track && select track)
    * `Filters` - Contains all possible filters for a song, podcast or playlist.
    * `Search Song` - Subclass which contains the implementation for searching a song with given filters.
    * `SearchCommand` - Contains the type of track and given filters by user and computes the search.
    * `SearchPodcast` - Subclass which contains the implementation for searching a podcast with given filters.
    * `SearchPlaylist` - Subclass which contains the implementation for searching a playlist with given filters.
  * `mydata/` - Contains custom classes with specific fields.
    * `Song` - Extended class that contains all fields of a song from input and more particular fields & methods.
    * `Episode` - Extended class that contains data for an episode(Name, Duration, Description) and more particular fields & methods.
    * `Podcast` - Extended class that contains data for a podcast(Name, Owner Episodes) and and more particular fields & methods.
    * `Playlist` - Extended class that contains all data for a playlist & particular methods for the requests of users.
    * `InputData` - Class that contains all fields a command can have, used for reading commands by users.
  * `musicplayer/` - Contains the implementation for our player. 
    * `Playback` - Class that contains the implementation for our running playback in player.
    * `MusicPlayer` - Class that contains data for a player's musicplayer and their requests.
    * `AudioCollection` - General class for all audio entities and general methods.


## Program Flow

Execution starts in `Main.java`, where the commands are read using `InputData.java` class as an ArrayList.

Before iterating through the commands, we first create the custom `Podcast.java` ArrayList of podcasts,   
and for each user it's coresponding MusicPlayer (`MusicPlayer.java`) in an ArrayList. Also, we create  
a new ArrayList of `Playlists`, where we keep track of all created playlists (public or private).

We start iterating through commands, and for each, we find the coresponding MusicPlayer for the user who  
requested the command. Also for output, we need a new `ArrayNode` used for command status. For each command   
name, we call the coresponding functions and output a message and in success case, the result(s).

Each subclass of `AudioCollection.java` : `Episode.java`, `Playlist.java`, `Podcast.java`, `Song.java` is     
using for implementation the data from `Playback.java` for the corresponding user's request.

## Commands

* `search` : First, unload if there is a loaded track. Than search with given filters the tracks   
             by the given type (there are 2 subclass that extends the `SearchCommand` for easily handle the cases).

* `select` : First, check if a search was done before. Than output the name, and mark the select parameter.

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

* `getTop5Songs` : For this command, we add the input songs into a different array, which will be sorted
                   by the number of likes field.

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
