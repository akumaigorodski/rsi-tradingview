package rsitradingview


object RSITradingView:
  private val clipUpward: Double => Double = math.max(_, 0D)
  private val clipDownward: Double => Double = -math.min(_, 0D)

  private val calculate: (Double, Double) => Double = {
    case (_, downwardValue) if downwardValue == 0D => 100D
    case (upwardValue, _) if upwardValue == 0D => 0D
    case (up, down) => 100 - 100 / (1 + up / down)
  }

  def recreate(initHistory: Vector[Double], period: Int = 14): RSITradingView =
    require(initHistory.nonEmpty, "History can not be empty when recreating RSI")
    val diff = initHistory.drop(1).zip(initHistory).map(_ - _)

    val up = diff.map(clipUpward)
    val down = diff.map(clipDownward)

    val alpha = 1D / period
    val upEWMA = EWMA.startWith(up.head, alpha)
    val downEWMA = EWMA.startWith(down.head, alpha)

    val (upEWMAHistory, upEWMA1) = calculateInitEWMAHistory(up.tail, upEWMA)
    val (downEWMAHistory, downEWMA1) = calculateInitEWMAHistory(down.tail, downEWMA)
    val history = upEWMAHistory.zip(downEWMAHistory).map(calculate.tupled)
    RSITradingView(history, upEWMA1, downEWMA1)

  private def calculateInitEWMAHistory(moves: Vector[Double], ewma: EWMA): (Vector[Double], EWMA) =
    moves.foldLeft(Vector(ewma.value) -> ewma) { case (Tuple2(acc, ewma1), priceMove) =>
      val ewma2 = ewma1 updated priceMove
      (acc :+ ewma2.value, ewma2)
    }

case class RSITradingView(history: Vector[Double], upEWMA: EWMA, downEWMA: EWMA):
  // These will typically be "closing" values of two subsequent candles
  def update(current: Double, previous: Double): RSITradingView =
    update(current - previous)

  def update(lastDiff: Double): RSITradingView =
    val upEWMA1 = upEWMA updated RSITradingView.clipUpward(lastDiff)
    val downEWMA1 = downEWMA updated RSITradingView.clipDownward(lastDiff)
    val history1 = history :+ RSITradingView.calculate(upEWMA1.value, downEWMA1.value)
    RSITradingView(history1.tail, upEWMA1, downEWMA1)
