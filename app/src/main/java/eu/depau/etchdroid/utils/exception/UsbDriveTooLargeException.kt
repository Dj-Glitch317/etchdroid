package eu.depau.etchdroid.utils.exception

import android.content.Context
import eu.depau.etchdroid.R
import eu.depau.etchdroid.utils.exception.base.FatalException
import kotlinx.parcelize.Parcelize

/**
 * Exception thrown when the USB drive's block count overflows a signed 32-bit
 * integer (i.e. the drive is ≥ 2 TB).  libaums reports block counts as [Int],
 * so drives this large produce a negative value that cannot be handled safely.
 */
@Parcelize
class UsbDriveTooLargeException : FatalException(
    "USB drive is too large (>= 2 TB); block count overflows Int"
) {
    override fun getUiMessage(context: Context): String {
        return context.getString(R.string.the_usb_drive_is_too_large)
    }
}
