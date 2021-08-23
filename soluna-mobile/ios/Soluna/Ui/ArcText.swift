import SwiftUI

enum Direction {
    case down, up
}

struct ArcText : View {
    var text: String
    var radius: CGFloat
    var direction: Direction
    var renderCharacter: (String) -> Text = { Text($0) }
    
    @State private var textSizes: [CGSize] = []
    
    var body: some View {
        let characters = text.enumerated().map(ArcTextCharacter.init)
        return ZStack {
            ForEach(characters) { item in
                let sign = direction == .down ? 1 : -1
                
                TextSizeReader {
                    renderCharacter(String(item.character))
                }
                .offset(x: 0, y: CGFloat(sign) * (height(at: item.index) / 2 - radius))
                .rotationEffect(angle(at: item.index))
            }
        }
        .frame(width: 2*radius, height: 2*radius)
        .onPreferenceChange(TextSizePreferenceKey.self) {
            self.textSizes = $0
        }
    }
    
    private func width(at index: Int) -> CGFloat {
        textSizes.count > index ? textSizes[index].width : 0
    }
    
    private func height(at index: Int) -> CGFloat {
        textSizes.count > index ? textSizes[index].height : 0
    }
    
    private func angle(at index: Int) -> Angle {
        let letterWidths = textSizes.map { $0.width }

        let prevWidth =
            index < letterWidths.count ?
            letterWidths.dropLast(letterWidths.count - index).reduce(0, +) :
            0
        let prevArcWidth = Double(prevWidth / radius)
        let totalArcWidth = Double(letterWidths.reduce(0, +) / radius)
        let charWidth = width(at: index)
        let charOffset = Double(charWidth / 2 / radius)
        let arcCharCenteringOffset = -totalArcWidth / 2
        let charArcOffset = prevArcWidth + charOffset + arcCharCenteringOffset
        return Angle(radians:  (direction == .down ? charArcOffset : .pi - charArcOffset))
    }
}

private struct TextSizePreferenceKey: PreferenceKey {
    static var defaultValue: [CGSize] { [] }
    static func reduce(value: inout [CGSize], nextValue: () -> [CGSize]) {
        value.append(contentsOf: nextValue())
    }
}

private struct TextSizeReader<V: View>: View {
    var content: () -> V
    var body: some View {
        content()
            .background(GeometryReader { proxy in
                Color.clear.preference(key: TextSizePreferenceKey.self, value: [proxy.size])
            })
    }
}

private struct ArcTextCharacter : Identifiable {
    var index: Int
    var character: Character

    var id: String { "\(index)/\(character)" }
}

struct ArcText_Preview : PreviewProvider {
    static var previews: some View {
        Group {
            ArcText(text: "Text that curves down", radius: 100, direction: .down)
            ArcText(text: "Text that curves up", radius: 100, direction: .up)
                .rotationEffect(.degrees(180))
        }.previewLayout(.sizeThatFits)
    }
}
