# Audio & Video Files Directory

## Instructions for Adding Your Custom Media Files

This directory is where you should place your custom audio and video files for the Tetris game.

### Required Media Files:

1. **Menu Background Video** ⭐ NEW
   - File name: `menu_background.mp4`
   - Purpose: Animated background for the main menu (with 40% darker overlay)
   - Recommended: High-quality MP4, loops seamlessly
   - Duration: 10-30 seconds (will loop automatically)
   - Resolution: 800x600 or higher (will be scaled to fit)
   - Note: Video audio is muted automatically

2. **Menu Background Music** (Optional)
   - File name: `menu_background.mp3` (or .wav, .m4a)
   - Purpose: Background music for the main menu (separate from video)
   - Recommended: Calm, ambient music that loops well
   - Duration: 2-5 minutes (will loop automatically)

2. **Game Background Music** (Future implementation)
   - File name: `game_background.mp3`
   - Purpose: Background music during gameplay
   - Recommended: Upbeat, energetic music

3. **Sound Effects** (Future implementation)
   - `line_clear.wav` - When lines are cleared
   - `piece_drop.wav` - When piece lands
   - `piece_rotate.wav` - When piece rotates
   - `game_over.wav` - When game ends
   - `menu_click.wav` - Button click sounds

### Supported Media Formats:
**Video:**
- MP4 (recommended)
- AVI
- MOV

**Audio:**
- MP3 (recommended)
- WAV
- M4A
- AIFF

### File Size Recommendations:
- Keep video files under 50MB each
- Keep music files under 10MB each
- Keep sound effects under 1MB each
- Use compressed formats (MP4 for video, MP3 for audio)

### Current Implementation:
The MenuController.java file is set up to load:
1. `menu_background.mp4` - Video background (loops automatically, muted)
2. `menu_background.mp3` - Audio background (optional, loops automatically)

Simply add your files with these exact names and they will play automatically.

### Volume Settings:
- Background music is set to 30% volume by default
- You can adjust this in MenuController.java (line with `backgroundMusic.setVolume(0.3)`)

### Troubleshooting:
If audio doesn't play:
1. Check that the file name matches exactly (case-sensitive)
2. Ensure the file format is supported
3. Check the console for error messages
4. Verify the file isn't corrupted by playing it in another application
