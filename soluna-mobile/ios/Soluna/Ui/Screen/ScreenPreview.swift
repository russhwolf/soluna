import SwiftUI

struct ScreenPreview<Content: View>: View {
    let content: Content

    var body: some View {
        ForEach(ColorScheme.allCases, id : \.self) { colorScheme in
            ForEach([ContentSizeCategory.small, ContentSizeCategory.medium, ContentSizeCategory.large], id: \.self) { size in
//                ForEach(InterfaceOrientation.allCases, id: \.self) { orientation in
                    content
                        .preferredColorScheme(colorScheme)
                        .environment(\.sizeCategory, size)

//                }
            }
        }
    }
}

extension View {
    func screenPreview() -> some View {
        ScreenPreview(content: self)
    }
}
