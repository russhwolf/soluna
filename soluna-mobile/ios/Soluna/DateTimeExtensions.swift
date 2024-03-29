import Foundation
import Shared

extension Instant {
    func toDisplayTime(timeZone: Foundation.TimeZone) -> String {
        let formatter = DateComponentsFormatter()
        formatter.allowedUnits = [.weekday, .hour, .minute]
        formatter.unitsStyle = .positional
        let dateComponents = SwiftKotlinBridge().nsDateComponents(instant: self, timeZone: timeZone)
        
        return formatter.string(from: dateComponents) ?? "???" // TODO error handling
    }
}
