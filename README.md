Copyright 2022 Vasile Alexandru-Gabriel (alexandru.vasile03@stud.acs.upb.ro)

# GlobalWaves - Audio Player - Project III

## Design Patterns
1. `Singleton Pattern`   
This pattern is used in creating the `LibraryInput` with all the audio files and inital users.        
Because it remains the same throughout the program execution, the choice of using a Singleton pattern was obvious.

2. `Command Pattern`    
This pattern was used to encapsulate each command with its own class and execution method.      
It is used in `commands/` where we have a common inteface to implement and a super class for inheritance.

3. `Template Pattern`     
This pattern was used in the class `pages/UserPage`. Because in the next stage of project   
we might display more content of a user this pattern is convenient for later implementation of some methods.

4. `Strategy Pattern`   
This pattern was used in the class `recommendations/`. Because the platform can perform    
different types of recommendations for a specific user, to handle it easily, Strategy pattern was obvious.

5. `Observer Pattern`
This pattern was used in the class `notifications/`. Because subscriber notifications    
depend on the artist or host they've subscribed to, to easily update every notifications, Observer pattern was best for it.

## Structure Of The Project
  * `Commands/` - Directory that contains all possible commands a user can request from platform and their action.
  * `database/` - Directory that contains all types of tracks and the database.
    * `Song` - Extended class that contains all fields of a song from input and more particular fields & methods.
    * `Album` - Class that contains all fields an album can have and it's particular methods for player.
    * `Episode` - Extended class that contains data for an episode(Name, Duration, Description) and more particular fields & methods.
    * `Podcast` - Extended class that contains data for a podcast(Name, Owner Episodes) and and more particular fields & methods.
    * `Playlist` - Extended class that contains all data for a playlist & particular methods for the requests of users.
    * `MyDatabase` - Class that contains all data in program (users, audio files) and executes requests.
  * `musicplayer/` - Directory that contains the implementation for our player. 
    * `Playback` - Class that contains the implementation for our running playback in player.
    * `MusicPlayer` - Class that contains data for a player's musicplayer and their requests.
    * `AudioCollection` - General class for all audio entities and general methods.
  * `pages` - Directory that contains all types of pages implementation.
    * `UserPage` - General abstract class used to define all users involved in platform.
    * `HomePage` - Subclass that will always corespond to a normal user and some of his data.
    * `HostPage` - Subclass that will always corespond to a host and contains it's data.
    * `ArtistPage` - Subclass that will always corespond to an artist and contains it's data.
    * `LikedContentPage` - Subclass that will always corespond to a normal user. 
  * `searchbar/` - Directory that contains implementation for SearchBar commands (search track & select track).
    * `Search` - Contains general class for implementing the search command.
    * `Filters` - Contains all possible filters for a song, podcast, playlist or album.
    * `SearchSong` - Subclass which contains the implementation for searching a song with given filters.
    * `SearchHost` - Subclass which contains the implementation for searching a host with given filters.
    * `SearchAlbum` - Subclass which contains the implementation for searching an album with given filters.
    * `SearchArtist` - Subclass which contains the implementation for searching an artist with given filters.
    * `SearchPodcast` - Subclass which contains the implementation for searching a podcast with given filters.
    * `SearchPlaylist` - Subclass which contains the implementation for searching a playlist with given filters.
  * `users/` - Directory that contains all types of users.
    * `UserHost` - Subclass that defines a host with specific fields and methods.
    * `UserTypes` - General abstract class used to define all types of users.
    * `UserArtist` - Subclass that defines an artist with specific fields and methods.
    * `UserNormal` - Subclass that defines a normal user with specific fields and methods.
  * `recommendations/` - Directory that contains Strategy Design Pattern in implementing recommendations.
  * `notifications` - Directory that contains Observer Design Pattern in implementing notifications for a normal user.
 
## Program Flow

Execution starts in `Main.java`, where the commands are read using `CommandInput.java`      
class as an ArrayList and later be transformed into an array of specific commands that   
just calls their execute method.

We iterate through each command and call the `@execute` method using the `database`    
as an intermediary between a user's request and the platform's data itself. Thus,    
through the `database`, we add or delete entities and return the messages corresponding    
to the order.

Each subclass of `AudioCollection.java` : `Playlist.java`, `Podcast.java`,   
`Song.java`, `Album.java` is using for implementation the data from `Playback.java`    
for the corresponding user's request about it's current playing track in musicplayer.

For `Page.java` implementation we create through inheritance specific subclasses for    
all types of users(artists, hosts, normalUsers) with fields pointing to each user's fields for    
instant updates about each user's data.

To handle all the required statistics, recommendations and ad breaks, I added a queue in the   
musicplayer to easily insert the ad and for each normal user I added some fields for all his types
of history.

**NOTE: For more details about implementation check JavaDoc.**