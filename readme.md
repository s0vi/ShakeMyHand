# Shake My Hand!
A gentleman's agreement.

## What is it?
Shake My Hand (SMH) is a mod designed to do what so many other games have built in.
Modded Minecraft is enjoyed best with friends, but having to download modpacks and match mods
is annoying, especially for DIY modpacks. SMH is a server-client handshake that automatically
downloads and reloads Minecraft to match the mods on any server you join.

## Limitations

- Unknown Mods
  - SMH can only download mods it can find. I am actively reaching out to popular
  mod developers to add support, and working on some search functionality to match
  missing mods on its own.
- Changing Server IPs
  - Every time a server IP (whatever you type in the Direct Connect or Add Server box)
  changes, it doesn't change the name of the directory that Minecraft saves server info to.
  This may cause some modpacks to be downloaded twice.
  - In the future, SMH will track when you edit server entries in the multiplayer menu,
  and store modpacks more efficiently.

## Roadmap
  
- [ ] Alpha
  - [ ] Successfully inject into the Fabric Loader
  - [ ] Establish networking between server and client 
  - [ ] Establish a schema in `fabric.mod.json` to read download link
  - [ ] Download mods from given links
    - [ ] Modrinth
    - [ ] Curseforge
    - [ ] Github
  - [ ] Establish a way to restart the game
- [ ] 