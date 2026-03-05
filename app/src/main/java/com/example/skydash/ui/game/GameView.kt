package com.example.skydash.game

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import kotlin.math.*
import kotlin.random.Random

class GameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : SurfaceView(context, attrs), SurfaceHolder.Callback {

    private var thread: GameThread? = null

    private val bgPaint = Paint().apply { color = Color.parseColor("#0B1020") }
    private val playerPaint = Paint().apply { color = Color.parseColor("#00D1FF"); isAntiAlias = true }
    private val coinPaint = Paint().apply { color = Color.parseColor("#FFC107"); isAntiAlias = true }
    private val obstaclePaint = Paint().apply { color = Color.parseColor("#F44336"); isAntiAlias = true }
    private val textPaint = Paint().apply { color = Color.WHITE; textSize = 48f; isAntiAlias = true }
    private val shieldPaint = Paint().apply { color = Color.argb(100, 0, 209, 255); style = Paint.Style.STROKE; strokeWidth = 6f }

    private var widthF = 0f
    private var heightF = 0f

    private var playerX = 0f
    private var playerY = 0f
    private var playerR = 28f

    private var score = 0
    private var coins = 0
    private var timeAcc = 0f
    private var spawnAcc = 0f
    private var moveTargetX = 0f

    private data class Obj(var x: Float, var y: Float, var r: Float)

    private val coinList = mutableListOf<Obj>()
    private val obsList = mutableListOf<Obj>()
    private val sparks = mutableListOf<Obj>()

    private var speed = 360f
    private var playerSpeed = 800f
    private var magnet = 0f
    private var shield = 0f

    private var running = false
    var isPaused = false
        private set

    private val prefs = context.getSharedPreferences("skydash", Context.MODE_PRIVATE)

    init {
        holder.addCallback(this)
        isFocusable = true
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        widthF = width.toFloat()
        heightF = height.toFloat()
        reset()
        thread = GameThread(holder, this)
        running = true
        thread?.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
    override fun surfaceDestroyed(holder: SurfaceHolder) {
        running = false
        thread?.joinSafe()
    }

    fun onHostPause() { isPaused = true }
    fun onHostResume() { isPaused = false }
    fun togglePause() { isPaused = !isPaused }

    private fun reset() {
        playerX = widthF / 2f
        playerY = heightF * 0.8f
        moveTargetX = playerX
        score = 0
        coins = 0
        timeAcc = 0f
        spawnAcc = 0f

        coinList.clear()
        obsList.clear()
        sparks.clear()

        val upSpeed = prefs.getInt("up_speed", 0)
        val upMag = prefs.getInt("up_magnet", 0)
        val upShield = prefs.getInt("up_shield", 0)

        speed = 360f + upSpeed * 30f
        playerSpeed = 800f + upSpeed * 40f
        magnet = (upMag * 30).toFloat()
        shield = (upShield).toFloat()
    }

    fun update(dt: Float) {
        if (!running || isPaused) return

        timeAcc += dt
        spawnAcc += dt
        score = (timeAcc * 10).toInt()

        val difficulty = 1f + min(timeAcc / 60f, 2f)

        if (spawnAcc > 0.5f / difficulty) {
            spawnAcc = 0f
            if (Random.nextFloat() < 0.6f) {
                coinList.add(Obj(Random.nextFloat() * widthF, -20f, 18f))
            } else {
                obsList.add(Obj(Random.nextFloat() * widthF, -30f, 24f))
            }
        }

        val dx = moveTargetX - playerX
        val maxMove = playerSpeed * dt
        playerX += maxMove * sign(dx)
        if (abs(dx) <= maxMove) playerX = moveTargetX

        playerX = playerX.coerceIn(playerR, widthF - playerR)

        val fall = speed * dt
        coinList.forEach { it.y += fall }
        obsList.forEach { it.y += fall }

        if (magnet > 0f) {
            for (c in coinList) {
                val d = hypot((c.x - playerX), (c.y - playerY))
                if (d < 200f + magnet) {
                    val angle = atan2(playerY - c.y, playerX - c.x)
                    c.x += cos(angle) * 400f * dt
                    c.y += sin(angle) * 400f * dt
                }
            }
        }

        val itCoin = coinList.iterator()
        while (itCoin.hasNext()) {
            val c = itCoin.next()
            if (c.y - c.r > heightF) { itCoin.remove(); continue }
            if (circleHit(playerX, playerY, playerR, c.x, c.y, c.r)) {
                coins++
                spawnSparks(c.x, c.y)
                itCoin.remove()
            }
        }

        val itObs = obsList.iterator()
        while (itObs.hasNext()) {
            val o = itObs.next()
            if (o.y - o.r > heightF) { itObs.remove(); continue }
            if (circleHit(playerX, playerY, playerR, o.x, o.y, o.r)) {
                if (shield > 0f) {
                    spawnSparks(o.x, o.y)
                    itObs.remove()
                    shield -= 0.5f
                } else {
                    gameOver()
                    return
                }
            }
        }

        if (shield > 0f) shield = max(0f, shield - dt)

        val sIt = sparks.iterator()
        while (sIt.hasNext()) {
            val s = sIt.next()
            s.r -= 60f * dt
            s.y += 100f * dt
            if (s.r <= 0f) sIt.remove()
        }
    }

    private fun gameOver() {
        val high = prefs.getInt("highScore", 0)
        if (score > high) prefs.edit().putInt("highScore", score).apply()
        val totalCoins = prefs.getInt("coins", 0) + coins
        prefs.edit().putInt("coins", totalCoins).apply()

        isPaused = true
        reset()
        isPaused = false
    }

    private fun spawnSparks(x: Float, y: Float) {
        repeat(12) {
            sparks.add(Obj(x + Random.nextFloat()*12f-6f, y + Random.nextFloat()*12f-6f, 10f))
        }
    }

    private fun circleHit(ax: Float, ay: Float, ar: Float, bx: Float, by: Float, br: Float): Boolean {
        val dx = ax - bx
        val dy = ay - by
        val r = ar + br
        return dx*dx + dy*dy <= r*r
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
            moveTargetX = event.x
        }
        return true
    }

    fun render(canvas: Canvas) {
        canvas.drawRect(0f, 0f, widthF, heightF, bgPaint)

        val gridPaint = Paint().apply { color = Color.argb(30, 255, 255, 255); strokeWidth = 1f }
        val step = 60f
        var y = (-(System.currentTimeMillis() % 1000L) / 1000f * step)
        while (y < heightF) {
            canvas.drawLine(0f, y, widthF, y, gridPaint)
            y += step
        }

        for (c in coinList) canvas.drawCircle(c.x, c.y, c.r, coinPaint)
        for (o in obsList) canvas.drawCircle(o.x, o.y, o.r, obstaclePaint)

        canvas.drawCircle(playerX, playerY, playerR, playerPaint)
        if (shield > 0f) canvas.drawCircle(playerX, playerY, playerR + 12f, shieldPaint)

        for (s in sparks) {
            canvas.drawCircle(s.x, s.y, max(1f, s.r/2f), textPaint)
        }

        canvas.drawText("Score: $score", 16f, 56f, textPaint)
        canvas.drawText("Coins: $coins", 16f, 108f, textPaint)
    }

    private inner class GameThread(private val surface: SurfaceHolder, private val game: GameView)
        : Thread() {
        override fun run() {
            var last = System.nanoTime()
            while (running) {
                val now = System.nanoTime()
                val dt = ((now - last) / 1_000_000_000.0).toFloat()
                last = now

                if (!isPaused) game.update(dt)

                var canvas: Canvas? = null
                try {
                    canvas = surface.lockCanvas()
                    if (canvas != null) {
                        synchronized(surface) {
                            game.render(canvas)
                        }
                    }
                } finally {
                    if (canvas != null) surface.unlockCanvasAndPost(canvas)
                }
            }
        }
    }

    private fun Thread.joinSafe() {
        try { join() } catch (_: InterruptedException) {}
    }
}
