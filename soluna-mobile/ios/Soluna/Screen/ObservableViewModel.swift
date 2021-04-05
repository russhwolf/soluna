import SwiftUI
import Combine
import Shared

open class ObservableViewModel<State: AnyObject, Event: AnyObject, Action: AnyObject> : ObservableObject {
    @ObservedObject
    final var state: PublishedFlow<State>
    
    @Published
    private var event: Event? = nil

    private let viewModel: NativeViewModel<State, Event, Action>
    
    private var subscriptions: Set<AnyCancellable> = []
    
    init(_ viewModel: NativeViewModel<State, Event, Action>) {
        self.viewModel = viewModel
        state = PublishedFlow(createPublisher(viewModel.state), defaultValue: viewModel.initialState)
        
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
