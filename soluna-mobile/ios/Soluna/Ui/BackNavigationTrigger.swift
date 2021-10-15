import SwiftUI

class BackNavigationTrigger: ObservableObject {
    @Published
    fileprivate var trigger: Bool = false
    
    func navigate() {
        trigger = true
    }
    
    func reset() {
        trigger = false
    }
}

extension View {
    func bindBackNavigation(trigger: BackNavigationTrigger) -> some View {
        BackNavigationTriggerView(parent: self, trigger: trigger)
    }
}

private struct BackNavigationTriggerView<V: View> : View {
    
    let parent: V
    
    @Environment(\.presentationMode)
    var presentationMode: Binding
    
    @ObservedObject
    var trigger: BackNavigationTrigger

    var body: some View {
        parent
            .onReceive(trigger.$trigger, perform: { goBack in
                if goBack {
                    self.presentationMode.wrappedValue.dismiss()
                }
            })
    }
}
