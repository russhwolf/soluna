import SwiftUI
import Shared

struct SunMoonTimesGraphic : View {
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
    var currentTime: Instant
    var sunriseTime: Instant?
    var sunsetTime: Instant?
    var moonriseTime: Instant?
    var moonsetTime: Instant?
    var timeZone: Shared.TimeZone

    var geometry: GeometryProxy
    
    var body: some View {
        let smallTextFont = Font.body
        let largeTextFont = Font.title3
        
        // TODO lineHeight might not be correct here
        let smallTextSize = UIFont.preferredFont(forTextStyle: .body).lineHeight
        let largeTextSize = UIFont.preferredFont(forTextStyle: .title3).lineHeight

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
            ZStack {
                Circle()
                    .foregroundColor(backgroundColor)
                
                Line()
                    .stroke(style: StrokeStyle(lineWidth: midnightWidth, lineCap: .round))
                    .foregroundColor(backgroundColor)
                
                TimesArc(
                    currentTime: currentTime,
                    riseTime: sunriseTime,
                    setTime: sunsetTime,
                    timeZone: timeZone,
                    color: sunColor,
                    thickness: timesArcThickness,
                    radius: sunTimesRadius,
                    arcOffset: sunTimesOffset
                )

                TimesArc(
                    currentTime: currentTime,
                    riseTime: moonriseTime,
                    setTime: moonsetTime,
                    timeZone: timeZone,
                    color: moonColor,
                    thickness: timesArcThickness,
                    radius: moonTimesRadius,
                    arcOffset: moonTimesOffset
                )
                
                TimeIcon(
                    angle: sunriseTime?.toAngle(timeZone: timeZone),
                    radius: sunTimesRadius,
                    size: iconSize,
                    icon: "sun.max.fill"
                )
                TimeIcon(
                    angle: sunsetTime?.toAngle(timeZone: timeZone),
                    radius: sunTimesRadius,
                    size: iconSize,
                    icon: "sun.max"
                )
                TimeIcon(
                    angle: moonriseTime?.toAngle(timeZone: timeZone),
                    radius: moonTimesRadius,
                    size: iconSize,
                    icon: "moon.fill"
                )
                TimeIcon(
                    angle: moonsetTime?.toAngle(timeZone: timeZone),
                    radius: moonTimesRadius,
                    size: iconSize,
                    icon: "moon"
                )
                Line()
                    .stroke(style: StrokeStyle(lineWidth: currentTimeWidth, lineCap: CGLineCap.round))
                    .rotation(currentTime.toAngle(timeZone: timeZone))

            }.padding(outerPadding)
            
            ArcText(
                text: "Midnight",
                radius: diskRadius + smallTextSize,
                direction: .down
            ) { Text($0).font(smallTextFont) }

            ArcText(
                text: "Noon",
                radius: diskRadius + smallTextSize,
                direction: .up
            ) { Text($0).font(smallTextFont) }
                .rotationEffect(.degrees(180))
            
            ArcText(
                text: currentTime.toDisplayTime(timeZone: timeZone.toNSTimeZone()),
                radius: diskRadius + smallTextSize + largeTextSize,
                direction: (6..<18).contains(currentTime.toLocalDateTime(timeZone: timeZone).hour) ? .up : .down
            ) { Text($0).font(largeTextFont) }
                .rotationEffect(currentTime.toAngle(timeZone: timeZone))
        }
    }
}

private struct TimesArc : View {
    let currentTime: Instant
    let riseTime: Instant?
    let setTime: Instant?
    let timeZone: Shared.TimeZone
    let color: Color
    let thickness: CGFloat
    let radius: CGFloat
    let arcOffset: CGFloat
    
    var body: some View {
        if (riseTime != nil || setTime != nil) {
            let effectiveRiseTime = riseTime ?? currentTime
            let effectiveSetTime = setTime ?? currentTime.plusOneDay()
            let effectiveRiseAngle = effectiveRiseTime.toAngle(timeZone: timeZone)
            let effectiveSetAngle = effectiveSetTime.toAngle(timeZone: timeZone)

            let flip: Bool = riseTime != nil && setTime != nil
                && effectiveRiseAngle > effectiveSetAngle
            let unflippedMidpoint = (effectiveRiseAngle + effectiveSetAngle) / 2
            let midpoint = unflippedMidpoint + (flip ? .degrees(180.0) : .zero)

            Arc(
                startAngle: effectiveRiseAngle,
                endAngle: midpoint
            ).stroke(style: StrokeStyle(
                lineWidth: thickness,
                lineCap: riseTime != nil ? .round : .butt
            ))
            .rotation(Angle(degrees: -90))
            .padding(CGFloat(arcOffset))
            .foregroundColor(color)
            
            Arc(
                startAngle: midpoint,
                endAngle: effectiveSetAngle
            ).stroke(style: StrokeStyle(
                lineWidth: thickness,
                lineCap: setTime != nil ? .round : .butt
            ))
            .rotation(Angle(degrees: -90))
            .padding(CGFloat(arcOffset))
            .foregroundColor(color)
        }
    }
}

private struct TimeIcon : View {
    let angle: Angle?
    let radius: CGFloat
    let size: CGFloat
    let icon: String
    
    var body: some View {
        if let angle = angle {
            Image(systemName: icon)
                .resizable()
                .frame(width: size, height: size, alignment: /*@START_MENU_TOKEN@*/.center/*@END_MENU_TOKEN@*/)
                .position(
                    x: radius * CGFloat(sin(angle.radians)),
                    y: -radius * CGFloat(cos(angle.radians))
                )
                .frame(width: 0.4, height: 0.4, alignment: .topLeading) // TODO why do these disappear if width/height are 0 and we're in an if statement?
        }
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
    
    let startAngle: Angle
    let endAngle: Angle
    
    func path(in rect: CGRect) -> Path {
        var path = Path()
        path.addArc(
            center: CGPoint(x: rect.width / 2, y: rect.height / 2),
            radius: rect.width / 2,
            startAngle: startAngle,
            endAngle: endAngle,
            clockwise: false
        )
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
    
    private static let hourUnit = DateTimeUnit.TimeBased(nanoseconds: 1_000_000_000 * 60 * 60)
    
    static var previews: some View {
        ForEach(ColorScheme.allCases, id: \.self) { colorScheme in
            SunMoonTimesGraphic(
                currentTime: defaultCurrentTime,
                sunriseTime: defaultSunriseTime,
                sunsetTime: defaultSunsetTime,
                moonriseTime: defaultMoonriseTime,
                moonsetTime: defaultMoonsetTime,
                timeZone: defaultTimeZone
            )
            .preferredColorScheme(colorScheme)
            
//            SunMoonTimesGraphic(
//                currentTime: defaultCurrentTime,
//                sunriseTime: defaultSunriseTime,
//                sunsetTime: defaultSunsetTime,
//                moonriseTime: defaultMoonriseTime,
//                moonsetTime: defaultMoonsetTime,
//                timeZone: defaultTimeZone
//            )
//            .preferredColorScheme(colorScheme)
//            .environment(\.sizeCategory, .extraLarge)
            
            SunMoonTimesGraphic(
                currentTime: defaultCurrentTime,
                sunriseTime: defaultSunriseTime,
                sunsetTime: defaultSunsetTime,
                moonriseTime: defaultMoonriseTime,
                moonsetTime: defaultMoonsetTime,
                timeZone: defaultTimeZone
            )
            .preferredColorScheme(colorScheme)
            .environment(\.sizeCategory, .extraSmall)
            
            SunMoonTimesGraphic(
                currentTime: defaultCurrentTime.plus(value: 12, unit: hourUnit),
                sunriseTime: defaultSunriseTime,
                sunsetTime: defaultSunsetTime,
                moonriseTime: defaultMoonriseTime,
                moonsetTime: defaultMoonsetTime,
                timeZone: defaultTimeZone
            )
            .preferredColorScheme(colorScheme)
            
            SunMoonTimesGraphic(
                currentTime: defaultCurrentTime,
                sunriseTime: defaultSunriseTime,
                sunsetTime: defaultSunsetTime,
                moonriseTime: defaultMoonsetTime,
                moonsetTime: defaultMoonriseTime,
                timeZone: defaultTimeZone
            )
            .preferredColorScheme(colorScheme)
            
            SunMoonTimesGraphic(
                currentTime: defaultCurrentTime,
                sunriseTime: nil,
                sunsetTime: defaultSunsetTime,
                moonriseTime: defaultMoonriseTime,
                moonsetTime: defaultMoonsetTime,
                timeZone: defaultTimeZone
            )
            .preferredColorScheme(colorScheme)
            
            SunMoonTimesGraphic(
                currentTime: defaultCurrentTime,
                sunriseTime: defaultSunriseTime,
                sunsetTime: nil,
                moonriseTime: defaultMoonriseTime,
                moonsetTime: defaultMoonsetTime,
                timeZone: defaultTimeZone
            )
            .preferredColorScheme(colorScheme)
            
            SunMoonTimesGraphic(
                currentTime: defaultCurrentTime,
                sunriseTime: nil,
                sunsetTime: nil,
                moonriseTime: defaultMoonriseTime,
                moonsetTime: defaultMoonsetTime,
                timeZone: defaultTimeZone
            )
            .preferredColorScheme(colorScheme)
            
            SunMoonTimesGraphic(
                currentTime: defaultCurrentTime,
                sunriseTime: defaultSunriseTime,
                sunsetTime: defaultSunsetTime,
                moonriseTime: nil,
                moonsetTime: defaultMoonsetTime,
                timeZone: defaultTimeZone
            )
            .preferredColorScheme(colorScheme)
            
            SunMoonTimesGraphic(
                currentTime: defaultCurrentTime,
                sunriseTime: defaultSunriseTime,
                sunsetTime: defaultSunsetTime,
                moonriseTime: defaultMoonriseTime,
                moonsetTime: nil,
                timeZone: defaultTimeZone
            )
            .preferredColorScheme(colorScheme)
            
            SunMoonTimesGraphic(
                currentTime: defaultCurrentTime,
                sunriseTime: defaultSunriseTime,
                sunsetTime: defaultSunsetTime,
                moonriseTime: nil,
                moonsetTime: nil,
                timeZone: defaultTimeZone
            )
            .preferredColorScheme(colorScheme)
        }
        .frame(width: 400, height: 400, alignment: .center)
        .previewLayout(.sizeThatFits)
    }
}
