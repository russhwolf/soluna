import SwiftUI
import Shared

struct SunMoonTimesGraphic : View {
    @EnvironmentObject var theme: SolunaTheme
    
    var currentTime: Instant
    var sunriseTime: Instant?
    var sunsetTime: Instant?
    var moonriseTime: Instant?
    var moonsetTime: Instant?
    var timeZone: Shared.TimeZone
    
    @State
    private var radius = CGFloat(0.0)
    
    var body: some View {
        GeometryReader { geometry in
            SunMoonTimesGraphicWithGeometry(
                currentTime: currentTime,
                sunriseTime: sunriseTime,
                sunsetTime: sunsetTime,
                moonriseTime: moonriseTime,
                moonsetTime: moonsetTime,
                timeZone: timeZone,
                geometry: geometry
            )
        }
    }
}

private struct SunMoonTimesGraphicWithGeometry : View {
    @EnvironmentObject var theme: SolunaTheme
    
    var currentTime: Instant
    var sunriseTime: Instant?
    var sunsetTime: Instant?
    var moonriseTime: Instant?
    var moonsetTime: Instant?
    var timeZone: Shared.TimeZone

    var geometry: GeometryProxy
    
    var body: some View {
        let effectiveSunriseTime = sunriseTime ?? currentTime
        let effectiveSunsetTime = sunsetTime ?? currentTime.plusOneDay()
        let effectiveMoonriseTime = moonriseTime ?? currentTime
        let effectiveMoonsetTime = moonsetTime ?? currentTime.plusOneDay()
        
        // TODO theme colors
        let backgroundColor = Color(red: 0.5, green: 0.5, blue: 0.5, opacity: 0.5)
        let sunColor = SolunaTheme.Color.primary
        let moonColor = SolunaTheme.Color.secondary
        
        let minSize = min(geometry.size.width, geometry.size.height)
        let outerPadding = CGFloat(48.0)
        let timesArcThickness = CGFloat(32.0)
        let timesArcMargin = CGFloat(8.0)
            
        let diskRadius = minSize / 2 - outerPadding
        let moonTimesRadius = diskRadius - timesArcMargin - timesArcThickness / 2
        let sunTimesRadius = diskRadius - 2 * timesArcMargin - 3 * timesArcThickness / 2
        let moonTimesOffset = timesArcMargin + 0.5 * timesArcThickness
        let sunTimesOffset = 2 * timesArcMargin + 1.5 * timesArcThickness
        let currentTimeWidth = CGFloat(4.0)
        let midnightWidth = CGFloat(4.0)
        let iconSize = timesArcThickness * (1 - 2 * outerPadding/minSize)
        
        return ZStack {
            Circle()
                .foregroundColor(backgroundColor)
            
            Line()
                .stroke(style: StrokeStyle(lineWidth: midnightWidth, lineCap: CGLineCap.round))
                .foregroundColor(backgroundColor)
            
            Arc(startAngle: effectiveSunsetTime.toAngle(timeZone: timeZone), endAngle: effectiveSunriseTime.toAngle(timeZone: timeZone)).stroke(style: StrokeStyle(lineWidth: timesArcThickness, lineCap: CGLineCap.round)).rotation(Angle(degrees: -90)).padding(CGFloat(sunTimesOffset)).foregroundColor(sunColor)
            
            Arc(startAngle: effectiveMoonsetTime.toAngle(timeZone: timeZone), endAngle: effectiveMoonriseTime.toAngle(timeZone: timeZone)).stroke(style: StrokeStyle(lineWidth: timesArcThickness, lineCap: CGLineCap.round)).rotation(Angle(degrees: -90)).padding(CGFloat(moonTimesOffset)).foregroundColor(moonColor)
            
            TimeIcon(angle: effectiveSunriseTime.toAngle(timeZone: timeZone), radius: sunTimesRadius, size: iconSize, icon: "sun.max.fill")
            
            TimeIcon(angle: effectiveSunsetTime.toAngle(timeZone: timeZone), radius: sunTimesRadius, size: iconSize, icon: "sun.max")
            
            TimeIcon(angle: effectiveMoonriseTime.toAngle(timeZone: timeZone), radius: moonTimesRadius, size: iconSize, icon: "moon.fill")
            
            TimeIcon(angle: effectiveMoonsetTime.toAngle(timeZone: timeZone), radius: moonTimesRadius, size: iconSize, icon: "moon")
            
            Line()
                .stroke(style: StrokeStyle(lineWidth: currentTimeWidth, lineCap: CGLineCap.round))
                .rotation(currentTime.toAngle(timeZone: timeZone))

        }.padding(outerPadding)
    }
}

private struct TimeIcon : View {
    var angle: Angle
    var radius: CGFloat
    var size: CGFloat
    var icon: String
    
    var body: some View {
        Image(systemName: icon)
            .resizable()
            .frame(width: size, height: size, alignment: /*@START_MENU_TOKEN@*/.center/*@END_MENU_TOKEN@*/)
            .position(x: radius * CGFloat(sin(angle.radians)), y: -radius * CGFloat(cos(angle.radians)))
            .frame(width: 0, height: 0, alignment: .center)
    }
}

private struct Line : Shape {
    func path(in rect: CGRect) -> Path {
        var path = Path()
        path.move(to: CGPoint(x: rect.width / 2, y: rect.height / 2))
        path.addLine(to: CGPoint(x: rect.width / 2, y: rect.height / 2 - rect.width / 2))
        return path
    }
}

private struct Arc : Shape {
    
    @State
    var startAngle: Angle?
    
    @State
    var endAngle: Angle?
    
    func path(in rect: CGRect) -> Path {
        var path = Path()
        if let startAngle = startAngle, let endAngle = endAngle {
            path.addArc(center: CGPoint(x: rect.width / 2, y: rect.height / 2), radius: rect.width / 2, startAngle: startAngle, endAngle: endAngle, clockwise: true)
        }
        return path
    }
}

private extension Instant {
    func toAngle(timeZone: Shared.TimeZone) -> Angle {
        let localDateTime = toLocalDateTime(timeZone: timeZone)
        let localMidnightInstant =
            LocalDateTime(year: localDateTime.year, month: localDateTime.month, dayOfMonth: localDateTime.dayOfMonth, hour: 0, minute: 0, second: 0, nanosecond: 0).toInstant(timeZone: timeZone)

        return self.minus(other:  localMidnightInstant).toAngle()
    }
    
    func plusOneDay() -> Instant {
        return self.plus(value: 1, unit: DateTimeUnit.TimeBased(nanoseconds: 86400_000_000_000))
    }
}

// Kotlin Duration comes to Swift as unwrapped Long ie Int64
private extension Int64 {
    func toAngle() -> Angle {
        // Match internal duration logic to unpack Int64 value
        let value = Double(self >> 1)
        let isNanos = self & 1 == 0
        
        let dayLength = Double(86400 * 1000 * (isNanos ? 1_000_000 : 1))
        
        let positiveDuration = value < 0 ? value + dayLength : value
        
        return Angle(radians: 2 * Double.pi * positiveDuration / dayLength)
    }
}

struct SunMoonTimesGraphic_Previews : PreviewProvider {
    
    private static let defaultTimeZone =
        Shared.TimeZone.Companion().of(zoneId: "America/New_York")

    private static let defaultCurrentTime = LocalDateTime(year: 2021, monthNumber: 1, dayOfMonth: 1, hour: 11, minute: 0, second: 0, nanosecond: 0).toInstant(timeZone: defaultTimeZone)

    private static let defaultSunriseTime = LocalDateTime(year: 2021, monthNumber: 1, dayOfMonth: 2, hour: 7, minute: 30, second: 0, nanosecond: 0).toInstant(timeZone: defaultTimeZone)
    private static let defaultSunsetTime = LocalDateTime(year: 2021, monthNumber: 1, dayOfMonth: 1, hour: 18, minute: 0, second: 0, nanosecond: 0).toInstant(timeZone: defaultTimeZone)

    private static let defaultMoonriseTime = LocalDateTime(year: 2021, monthNumber: 1, dayOfMonth: 2, hour: 9, minute: 0, second: 0, nanosecond: 0).toInstant(timeZone: defaultTimeZone)
    
    private static let defaultMoonsetTime = LocalDateTime(year: 2021, monthNumber: 1, dayOfMonth: 1, hour: 20, minute: 0, second: 0, nanosecond: 0).toInstant(timeZone: defaultTimeZone)
    
    static var previews: some View {
        ForEach(ColorScheme.allCases, id: \.self) {
            SunMoonTimesGraphic(currentTime: defaultCurrentTime, sunriseTime: defaultSunriseTime, sunsetTime: defaultSunsetTime, moonriseTime: defaultMoonriseTime, moonsetTime: defaultMoonsetTime, timeZone: defaultTimeZone
            )
            .preferredColorScheme($0)
        }
    }
}
