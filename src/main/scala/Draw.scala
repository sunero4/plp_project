object Draw {
  def findYCoordinate(x0: Int, y0: Int, x1: Int, y1: Int, x: Int): Int = if (x1 == x0) {
    y0
  } else {
    Math.round((y1.toFloat - y0) / (x1.toFloat - x0) * (x - x0) + y0)
  }

  def findXCoordinate(x0: Int, y0: Int, x1: Int, y1: Int, y: Int): Int = if (y1 == y0) {
    x0
  } else {
    (x0 * y1 - x1 * y0 + (x1 - x0) * y) / (y1 - y0)
  }

  def drawLine(x0: Int, y0: Int, x1: Int, y1: Int): CustomList[Coordinate] = if (x1 - x0 > y1 - y0) {
    CustomList.range(x0, x1).map(x => (x, findYCoordinate(x0, y0, x1, y1, x))).map(coordinate => new Coordinate(coordinate._1, coordinate._2))
    } else {
    CustomList.range(y0, y1).map(y => (findXCoordinate(x0, y0, x1, y1, y), y)).map(coordinate => new Coordinate(coordinate._1, coordinate._2))
    }

  def drawRectangle(x0: Int, y0: Int, x1: Int, y1: Int): CustomList[Coordinate] = if (x0 == x1 && y0 == y1) {
    Cons((x0, y1), Nil()).map(coordinate => new Coordinate(coordinate._1, coordinate._2))
  } else {
    drawLine(x0, y0, x0, y1).merge(drawLine(x0, y0, x1, y0)).merge(drawLine(x1, y0, x1, y1)).merge(drawLine(x0, y1, x1, y1))
  }

  @scala.annotation.tailrec
  def drawCircleRec(centre_x: Int, centre_y: Int, radius: Int, x: Int, y: Int, p: () => Int, coords: CustomList[(Int, Int)]): CustomList[(Int, Int)] = {
    if (x < y) {
      return coords
    }

    val newX = if (p() <= 0) x else x - 1
    val coordinates: CustomList[(Int, Int)] = coords
      .merge(Cons((newX + centre_x, y + centre_y), Cons((-newX + centre_x, y + centre_y), Cons((newX + centre_x, -y + centre_y), Cons((-newX + centre_x, -y + centre_y), Nil())))))
      .mergeIf(Cons((y + centre_x, newX + centre_y), Cons((-y + centre_x, newX + centre_y), Cons((y + centre_x, -newX + centre_y), Cons((-y + centre_x, -newX + centre_y), Nil())))), () => newX != y)

    if (p() <= 0) {
      drawCircleRec(centre_x, centre_y, radius, newX, y + 1, () => p() + 2 * y + 1, coordinates)
    } else {
      drawCircleRec(centre_x, centre_y, radius, newX, y + 1, () => p() + 2 * y - 2 * newX + 1, coordinates)
    }
  }

  def drawCircle(centre_x: Int, centre_y: Int, radius: Int): CustomList[Coordinate] = {
    val x = radius
    val y = 0

    val coords: CustomList[(Int, Int)] = Cons((x + centre_x, y + centre_y), Nil())
      .appendIf((x + centre_x, -y + centre_y), () => radius > 0)
      .appendIf((-x + centre_x, y + centre_y), () => radius > 0)
      .appendIf((y + centre_x, -x + centre_y), () => radius > 0)
      .appendIf((y + centre_x, x + centre_y), () => radius > 0)
      .appendIf((-y + centre_x, x + centre_y), () => radius > 0)

    drawCircleRec(centre_x, centre_y, radius, x, y + 1, () => 1 - radius, coords).map(coordinate => new Coordinate(coordinate._1, coordinate._2))
  }


  def fillObject(seed_x: Int, seed_y: Int, objectCoords: CustomList[Coordinate], fillCoords: CustomList[Coordinate]): CustomList[Coordinate] = {

    if(objectCoords.find(new Coordinate(seed_x, seed_y)) || fillCoords.find(new Coordinate(seed_x, seed_y)))
      return fillCoords

    fillObject(seed_x, seed_y + 1, objectCoords, fillObject(seed_x + 1, seed_y, objectCoords, fillObject(seed_x, seed_y - 1, objectCoords, fillObject(seed_x - 1, seed_y, objectCoords, fillCoords.append(new Coordinate(seed_x, seed_y))))))
  }
}
