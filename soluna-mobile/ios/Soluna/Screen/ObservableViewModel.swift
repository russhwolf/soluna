import SwiftUI
import Combine
import Shared

open class ObservableViewModel<State: AnyObject, Event: AnyObject, Action: AnyObject> : ObservableObject {
    @Published
    var state: State
    
    @Published
    private var event: Event? = nil

    private let viewModel: NativeViewModel<State, Event, Action>
    
    private var subscriptions: Set<AnyCancellable> = []
    
    init(_ viewModel: NativeViewModel<State, Event, Action>) {
        self.viewModel = viewModel
        state = viewModel.initialState
    }
    
    final func activate() {
        viewModel.activate()
        
        createPublisher(viewModel.state)
            .replaceError(with: viewModel.initialState)
            .compactMap { $0 }
            .receive(on: DispatchQueue.main)
            .assign(to: &$state)

        createPublisher(viewModel.events)
            .sink(receiveCompletion: { _ in }, receiveValue: { event in
                self.event = event
            })
            .store(in: &subscriptions)
        
        self.$event.sink { event in
            if let event = event {
                self.onEvent(event)
                self.event = nil
            }
        }.store(in: &subscriptions)
    }
    
    final func deactivate() {
        subscriptions.forEach { cancellable in
            cancellable.cancel()
        }
        viewModel.dispose()
        reset()
    }
        
    open func onEvent(_ event: Event) {
        
    }
    
    open func reset() {
        
    }
    
    final func performAction(action: Action) {
        createPublisher(viewModel.performAction(action: action))
            .sink(receiveCompletion: { _ in }, receiveValue: { _ in })
            .store(in: &subscriptions)
    }
    
}

extension View {
    func bindModel<State, Event, Action>(_ observableModel: ObservableViewModel<State, Event, Action>) -> some View {
        
        return self
            .onAppear { observableModel.activate() }
            .onDisappear { observableModel.deactivate() }
    }
}
