# ImageSearch
Search Images from Imgur based on query parameter

Search Gallery Images from Imgur based on keywords / search parameters.

Implemented Recycler view to populate teh results. Used Async task, Retrofit and Picasso library to make HTTP requests, parse Json on background thread and load the images. I have also cached the images in picasso for faster loading.

User can see the history of the previous searches made in the Search View.

The list shown on app load is based on images that are viral on imgur. Clicking on an image will show the image in fullscreen. 
