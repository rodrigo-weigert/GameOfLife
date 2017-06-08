# GameOfLife

This is a very simple implementation of [Conway's Game of Life](https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life) using JavaFX.
I coded this for fun a couple of years ago. It probably has bugs. The code isn't documented but it shouldn't be so hard to understand, if you
really want to try.

## Requirements
It requires Java 8 (or newer, I suppose) and JavaFX, if it doesn't already come with your installation of Java.

## How to Run

```java -jar gameoflife.jar```

or just double click `gameoflife.jar`, but this doesn't work on all OS.

You can also specify the screen size (in cells) and the cell side length (in pixels) via argument:

``` java -jar gameoflife.jar WIDTH HEIGHT LENGTH ```

Default for these are 200, 128, and 5, respectively.

## How to Use

Left mouse button to set cell alive, right mouse button to set cell dead. You can hold and drag the mouse around.


![Screenshot of the program](screenshot.png?raw=true "Screenshot")
