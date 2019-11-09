package come.texi.driver.wheel

interface OnWheelClickedListener {
    /**
     * Callback method to be invoked when current item clicked
     * @param wheel the wheel view
     * @param itemIndex the index of clicked item
     */
    fun onItemClicked(wheel: WheelView, itemIndex: Int)
}
