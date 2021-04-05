import Combine
import Shared

func createPublisher<T>(_ flowAdapter: FlowAdapter<T>) -> AnyPublisher<T, KotlinError> {
    return Deferred<Publishers.HandleEvents<PassthroughSubject<T, KotlinError>>> {
        let subject = PassthroughSubject<T, KotlinError>()
        let job = flowAdapter.subscribe(
            onEvent: { (item) in let _ = subject.send(item) },
            onError: { (error) in subject.send(completion: .failure(KotlinError(error))) },
            onComplete: { subject.send(completion: .finished) }
        )
        return subject.handleEvents(receiveCancel: {
            job.cancel(cause: nil)
        })
    }.eraseToAnyPublisher()
}

// TODO convert to async/await eventually
func createPublisher<T>(_ suspendAdapter: SuspendAdapter<T>) -> AnyPublisher<T, KotlinError> {
    return Deferred<Publishers.HandleEvents<PassthroughSubject<T, KotlinError>>> {
        let subject = PassthroughSubject<T, KotlinError>()
        let job = suspendAdapter.subscribe(
            onSuccess: { (item) in
                let _ = subject.send(item)
                subject.send(completion: .finished)
            },
            onError: { (error) in subject.send(completion: .failure(KotlinError(error))) }
        )
        return subject.handleEvents(receiveCancel: {
            job.cancel(cause: nil)
        })
    }.eraseToAnyPublisher()
}

class PublishedFlow<T> : ObservableObject {
    @Published
    var output: T
    
    init<E>(_ publisher: AnyPublisher<T, E>, defaultValue: T) {
        output = defaultValue
        
        publisher
            .replaceError(with: defaultValue)
            .compactMap { $0 }
            .receive(on: DispatchQueue.main)
            .assign(to: &$output)
    }
}

class KotlinError: LocalizedError {
    let throwable: KotlinThrowable
    init(_ throwable: KotlinThrowable) {
        self.throwable = throwable
    }
    var errorDescription: String? {
        get { throwable.message }
    }
}
