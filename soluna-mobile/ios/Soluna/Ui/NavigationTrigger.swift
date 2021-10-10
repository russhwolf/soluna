import SwiftUI

class NavigationTrigger<T: Hashable>: ObservableObject {
    @Published
    fileprivate var trigger: T? = nil
    
    func navigate(_ selector: T) {
        trigger = selector
    }
    
    func reset() {
        trigger = nil
    }
}

extension NavigationTrigger {
    func createLink<V: View>(tag: T, destination: @escaping () -> V) -> some View {
        NavigationTriggerView(trigger: self, tag: tag, destination: destination)
    }
}

extension NavigationTrigger where T == Bool {
    func navigate() {
        navigate(true)
    }
    
    func createLink<V: View>(destination: @escaping () -> V) -> some View {
        createLink(tag: true, destination: destination)
    }
}

private struct NavigationTriggerView<T: Hashable, V: View> : View {
    @ObservedObject
    var trigger: NavigationTrigger<T>

    var tag: T

    var destination: () -> V

    var body: some View {
        NavigationLink(
            destination: destination(),
            tag: tag,
            selection: $trigger.trigger,
            label: { EmptyView() }
        )
    }
}
