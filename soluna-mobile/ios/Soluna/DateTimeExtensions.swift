import Foundation
import Shared

extension Kotlinx_datetimeInstant {
    func toDisplayTime() -> String {
        let formatter = DateFormatter()
        formatter.dateStyle = .none
        formatter.timeStyle = .short

        let date = SwiftKotlinBridge().nsDate(instant: self)
        
        return formatter.string(from: date)
    }
}
