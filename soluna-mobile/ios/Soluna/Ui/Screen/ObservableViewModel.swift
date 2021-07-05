import SwiftUI
import Combine
import Shared

class ObservableViewModel<State: AnyObject, Event: AnyObject, Action: AnyObject> : ObservableObject {
    @Published
    var state: State

    private let viewModel: NativeViewModel<State, Event, Action>
    
    private var subscriptions: Set<AnyCancellable> = []
    
    init(_ viewModel: BaseViewModel<State, Event, Action>) {
        self.viewModel = NativeViewModel(delegate: viewModel)
        state = self.viewModel.initialState
    }
    
    final func activate() {
        reset()
        viewModel.activate()
        
        createPublisher(viewModel.state)
            .assertNoFailure()
            .compactMap { $0 }
            .receive(on: DispatchQueue.main)
            .sink(receiveCompletion: { _ in }, receiveValue: { [weak self] state in
                self?.state = state
            })
            .store(in: &subscriptions)

        createPublisher(viewModel.events)
            .sink(receiveCompletion: { _ in }, receiveValue: { [weak self] event in
                self?.onEvent(event)
            })
            .store(in: &subscriptions)
    }
    
    final func deactivate() {
        subscriptions.forEach { cancellable in
            cancellable.cancel()
        }
        subscriptions.removeAll()
        viewModel.dispose()
    }
        
    func onEvent(_ event: Event) {
        
    }
    
    func reset() {
        
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
