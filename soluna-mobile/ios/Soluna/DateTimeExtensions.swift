import Foundation
import Shared

extension Kotlinx_datetimeInstant {
    func toDisplayTime(timeZone: TimeZone) -> String {
        let formatter = DateComponentsFormatter()
        formatter.allowedUnits = [.weekday, .hour, .minute]
        formatter.unitsStyle = .positional
        let dateComponents = SwiftKotlinBridge().nsDateComponents(instant: self, timeZone: timeZone)
        
        return formatter.string(from: dateComponents) ?? "???" // TODO error handling
    }
}
