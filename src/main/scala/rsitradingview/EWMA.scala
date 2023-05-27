package rsitradingview

import scala.annotation.targetName
import scala.concurrent.duration.FiniteDuration


object EWMA:
  private val LogOf2: Double = 0.69315

  def computeAlpha(halfLife: FiniteDuration, interval: FiniteDuration): Double =
    val power = (LogOf2 / halfLife.toMillis) * interval.toMillis * -1
    1 - math.exp(power)

  def startWith(value: Double, alpha: Double): EWMA =
    require(alpha >= 0 && alpha <= 1, "Alpha must be in [0, 1]")
    EWMA(value, alpha)


case class EWMA(value: Double, alpha: Double):
  def updated(xn: Double): EWMA = copy(value = alpha * xn + (1 - alpha) * value)
