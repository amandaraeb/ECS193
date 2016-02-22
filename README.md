# ECS193
Linked Data Scanner

#####INSTRUCTIONS FOR CLONING FROM GITHUB TO ANDROID STUDIO:
1. File->New->Project from Version Control->Github
    * NOTE: When the project is first cloned, it WILL fail! The volley submodule will not be loaded.
2. View files using Project view (top left dropdown menu, make sure Project tab is open)
3. Make sure that the volley directory is empty (delete all files inside if not)
4. Open up Android Studio's terminal (bottom left tab)
5. Run the command: git submodule update --init --recursive
6. Tools->Android->Sync Project with Gradle Files
