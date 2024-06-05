package com.example.frametext.shapes.edge


class StarSearchDetails internal constructor(
    val widthHeight: Float,
    val innerRadius: Float,
    val startAngle: Double,
    val spikes: Int
) {

    override fun equals(other: Any?): Boolean {
        if (other is StarSearchDetails) {
            return if (widthHeight == other.widthHeight) {
                if (innerRadius == other.innerRadius) {
                    if (startAngle == other.startAngle) {
                        spikes == other.spikes
                    } else {
                        false
                    }
                } else {
                    false
                }
            } else {
                false
            }
        }
        return false
    }

    override fun hashCode(): Int {
        var hashcode = 1430287
        hashcode = hashcode * 7302013 xor widthHeight.toString().hashCode()
        hashcode = hashcode * 7302013 xor innerRadius.toString().hashCode()
        hashcode = hashcode * 7302013 xor startAngle.toString().hashCode()
        hashcode = hashcode * 7302013 xor spikes.toString().hashCode()
        return hashcode
    }

}