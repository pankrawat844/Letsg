package come.texi.driver.wheel

interface OnWheelScrollListener {
    /**
     * Callback method to be invoked when scrolling started.
     * @param wheel the wheel view whose state has changed.
     */
    fun onScrollingStarted(wheel: WheelView)

    /**
     * Callback method to be invoked when scrolling ended.
     * @param wheel the wheel view whose state has changed.
     */
    fun onScrollingFinished(wheel: WheelView)
}
