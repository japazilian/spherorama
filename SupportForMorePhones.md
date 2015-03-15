# Functions #

  * CamLayer.java, onSurfaceCreated function:
    * The values given here were hard coded for a nexus one. You will need to figure out which values to provide for these two settings.
      * For the preview size, you want to find something that can easily be resized into a new image of power of two dimensions.
      * For the picture size, just use the highest resolution that maintains a 1x1.5 ratio
  * Octogon.java, onPreviewFrame function:
    * The byte array needs to be of size m\*m where m is a power of two. You need to find a good way to resize the array to fill the "length" of the image represented by the byte array, and also make sure to resize the "height" by the same ratio. You then need to copy that array over at an offset so that it appears in the center of the byte array image.